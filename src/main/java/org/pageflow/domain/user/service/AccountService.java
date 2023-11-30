package org.pageflow.domain.user.service;


import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.exception.nosuchentity.NoSuchEntityException;
import org.pageflow.domain.user.constants.Role;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.model.dto.ProfileUpdateForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.AwaitingVerificationEmailRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.infra.email.EmailRequest;
import org.pageflow.infra.email.EmailSender;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.repository.FileMetadataRepository;
import org.pageflow.infra.file.service.FileService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    @Getter
    private final AccountRepository accountRepository;
    private final AwaitingVerificationEmailRepository emailCacheRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final TemplateEngine templateEngine;
    private final EmailSender emailSender;
    private final FileService fileService;
    private final CustomProperties customProperties;


    public void register(@Valid AdditionalSignupAccountDto form) {

        Profile profile = Profile.builder()
                .nickname(form.getNickname())
                .build();


        Account account = Account.builder()
                .provider(form.getProvider())
                .email(form.getEmail())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .profile(profile)
                .role(form.getUsername().equals("admin") ? Role.ADMIN : Role.USER)
                .build();

        Account savedAccount = accountRepository.save(account);

        MultipartFile profileImg = form.getProfileImg();
        String profileImgUrl = form.getProfileImgUrl();

        boolean isProfileImgEmpty = profileImg == null || profileImg.isEmpty();
        boolean isProfileImgUrlEmpty = profileImgUrl == null || profileImgUrl.isEmpty();
        
        // 프로필 사진 이미지 파일을 등록한 경우
        if (!isProfileImgEmpty && isProfileImgUrlEmpty) {
            setProfileImg(profileImg, savedAccount.getProfile());

            // 프로필 사진의 URL을 등록한 경우
        } else if (isProfileImgEmpty && !isProfileImgUrlEmpty) {
            setProfileImgUrl(profileImgUrl, savedAccount.getProfile());

            // 둘 다 등록한 경우 -> 파일로 등록한 사진이 우선순위
        } else if (!isProfileImgEmpty) { // && !isProfileImgUrlEmpty
            setProfileImg(profileImg, savedAccount.getProfile());

            // 프로필 사진을 등록하지 않은 경우
        } else {
            setProfileImgUrl(customProperties.getDefaults().getDefaultUserProfileImg(), savedAccount.getProfile());
        }
    }


    /**
     * Profile은 같이 업데이트 하지 않음
     */
    @Transactional
    public void updateAccount(AccountDto accountDto) {
        Account account = accountRepository.findByUsername(accountDto.getUsername());
        if(accountDto.getUsername() != null) account.setUsername(accountDto.getUsername());
        if(accountDto.getPassword() != null) account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        if(accountDto.getProvider() != null) account.setProvider(accountDto.getProvider());
        if(accountDto.getEmail() != null) account.setEmail(accountDto.getEmail());
    }

    
    @Transactional
    public Profile updateProfile(ProfileUpdateForm form) {
        Profile savedProfile = profileRepository.findById(form.getId()).orElseThrow(
                () -> new NoSuchEntityException(Profile.class)
        );
        savedProfile.setNickname(form.getNickname());
        
        if(!form.getProfileImg().isEmpty()){
            setProfileImg(form.getProfileImg(), savedProfile);
        }
        return savedProfile;
    }
    
    
    /**
     * send email for verifying email
     *
     * @param toEmail  이메일 인증 요청을 보낼 이메일 주소
     * @param authCode 인증코드
     */
    public void sendEmailVerifyingEmail(String toEmail, String authCode) {

        String authenticationUrl = customProperties.getSite().getBaseUrl() + "/signup?code=" + authCode + "&email=" + toEmail;

        // 인증 코드를 템플릿에 담아서 이메일 내용 생성
        Context context = new Context();
        context.setVariable("authenticationUrl", authenticationUrl);
        String emailText = templateEngine.process("/email/email_verify_template", context);

        // 이메일 발송
        emailSender.sendMail(
                new EmailRequest(customProperties.getEmail().getEmailVerifySender(), toEmail, "Pageflow 회원가입 이메일 인증"),
                emailText
        );
    }


    /**
     * 프로필 사진 설정
     * 프로필 사진을 FileMetadata와 함께 저장하고 uri를 반환
     */
    @Transactional
    public String setProfileImg(MultipartFile profileImg, Profile profile) {
        String staleProfileImgUrl = profile.getProfileImgUrl();
        
        // 프로필 이미지가 없거나 기본 이미지가 아닌 경우, 기존에 있던 프로필 사진은 삭제한다.
        if(!isDefaultProfileImgOrNullOrOAuth2ProfileImg(staleProfileImgUrl)){
            fileService.deleteFile(fileService.getPureFilePath(staleProfileImgUrl));
        }
        
        FileMetadata profileImgMetadata = fileService.uploadFile(profileImg, profile, FileMetadataType.PROFILE_IMG);
        String imgUri = fileService.getImgUri(profileImgMetadata);
        profile.setProfileImgUrl(imgUri);
        profileRepository.save(profile);
        return imgUri;
    }

    
    /**
     * 프로필 사진 설정
     * 프로필 사진 URl을 받고, 해당 URL을 Profile 엔티티에 저장
     */
    @Transactional
    public String setProfileImgUrl(String profileImgUrl, Profile profile) {
        
        profile.setProfileImgUrl(profileImgUrl);
        profileRepository.save(profile);
        return profileImgUrl;
    }
    
    
    public boolean isDefaultProfileImgOrNullOrOAuth2ProfileImg(String profileImgUrl) {
        return profileImgUrl == null
                || profileImgUrl.equals(customProperties.getDefaults().getDefaultUserProfileImg())
                || !profileImgUrl.startsWith(customProperties.getFiles().getImg().getBaseUrl());
    }

    public Account findAdminAccount() {
        return accountRepository.findByRole(Role.ADMIN).orElseThrow(
                () -> new NoSuchEntityException(Account.class, "관리자 계정을 찾을 수 없습니다.")
        );
    }
    
    public Account repoFindFetchJoinProfileByUsername(String username) {
        return accountRepository.findFetchJoinProfileByUsername(username);
    }

    public boolean repoExistsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    public boolean repoExistsByEmailAndProvider(String email, String provider) {
        return accountRepository.existsByEmailAndProvider(email, provider);
    }

}
