package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.exception.DomainError;
import org.pageflow.base.exception.DomainException;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.infra.email.EmailRequest;
import org.pageflow.infra.email.EmailSender;
import org.pageflow.infra.file.repository.FileMetadataRepository;
import org.pageflow.infra.file.service.FileService;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DefaultUserService implements UserService {
    
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final EmailSender emailSender;
    private final FileService fileService;
    private final CustomProperties customProperties;
    
    
    /**
     * @param form 회원가입 폼
     * @param userRole 사용자에게 부여할 권한
     * @return 영속화된 계정
     */
    @Override
    public Account signup(SignupForm form, ProviderType provider, RoleType userRole) {
        
        // username 검사
        validateUsername(form.getUsername());
        // email 검사
        validateEmail(form.getEmail());
        // password 검사
        validatePassword(form.getPassword(), form.getPasswordConfirm());
        // penname 검사
        validatePenname(form.getPenname());
        
        // 프로필 생성
        Profile profile = Profile.builder()
                .penname(form.getPenname())
                // 프로필 사진을 등록하지 않은 경우, 기본 이미지로 설정한다.
                .profileImgUrl(Objects.requireNonNullElse(form.getProfileImgUrl(), customProperties.getDefaults().getDefaultUserProfileImg()))
                .build();
        
        // 계정 생성
        Account account = Account.builder()
                .provider(provider)
                .email(form.getEmail())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(userRole)
                .build();
        
        return saveUser(account, profile);
    }
    
    public Account saveUser(Account account, Profile profile) {
        
        // Account 먼저 영속
        Account savedAccount = accountRepository.save(account);
        profile.associateAccount(savedAccount);
        
        // Profile 영속
        Profile savedProfile = profileRepository.save(profile);
        
        return savedProfile.getAccount();
    }
    
    // username 검사
    public void validateUsername(String username) {
        
        // 1. null, 공백문자 검사
        if(!StringUtils.hasText(username)){
            throw new DomainException(DomainError.User.BLANK_USERNAME);
        }
        
        // 2. username 정규식 검사
        if(!username.matches(UserSignupPolicy.USERNAME_REGEX)) {
            throw new DomainException(DomainError.User.USERNAME_REGEX_NOT_MATCH);
        }
        
        // 3. 사용할 수 없는 username 검사
        for(String invalidUsername : UserSignupPolicy.INVALID_USERNAME) {
            if(username.contains(invalidUsername)) {
                throw new DomainException(DomainError.User.UNUSEABLE_USERNAME, invalidUsername);
            }
        }
        
        // 4. username 중복 검사
        if(accountRepository.existsByUsername(username)){
            throw new DomainException(DomainError.User.DUPLICATE_USERNAME);
        }
    }
    
    // email 검사
    public void validateEmail(String email) throws DomainException {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(email)){
            throw new DomainException(DomainError.User.BLANK_EMAIL);
        }
        
        // 2. email 형식 검사
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw new DomainException(DomainError.User.EMAIL_REGEX_NOT_MATCH);
        }
        
        // 3. email 중복 검사
        if(accountRepository.existsByEmailAndEmailVerified(email, true)){
            throw new DomainException(DomainError.User.DUPLICATE_EMAIL);
        }
    }
    
    // password 검사
    public void validatePassword(String password, @Nullable String passwordConfirm) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(password)){
            throw new DomainException(DomainError.User.BLANK_PASSWORD);
        }
        
        // 2. password 정규식 검사
        if(!password.matches(UserSignupPolicy.PASSWORD_REGEX)) {
            throw new DomainException(DomainError.User.PASSWORD_REGEX_NOT_MATCH);
        }
        
        // 3. password와 passwordConfirm이 일치 검사
        if(passwordConfirm != null && !password.equals(passwordConfirm)){
            throw new DomainException(DomainError.User.PASSWORD_CONFIRM_NOT_MATCH);
        }
    }
    
    // penname 검사
    public void validatePenname(String penname) throws DomainException {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(penname)){
            throw new DomainException(DomainError.User.BLANK_PENNAME);
        }
        
        // 2. penname 정규식 검사
        if(!penname.matches(UserSignupPolicy.PENNAME_REGEX)) {
            throw new DomainException(DomainError.User.PENNAME_REGEX_NOT_MATCH);
        }
        
        // 3. 사용할 수 없는 필명
        for(String invalidPenname : UserSignupPolicy.INVALID_PENNAME) {
            if(penname.contains(invalidPenname)) {
                throw new DomainException(DomainError.User.UNUSEABLE_PENNAME, invalidPenname);
            }
        }
        
        // 4. penname 중복 검사
        if(profileRepository.existsByPenname(penname)){
            throw new DomainException(DomainError.User.DUPLICATE_PENNAME);
        }
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

//    public String changeProfileImg(MultipartFile profileImg, Profile profile) {
//        String staleProfileImgUrl = profile.getProfileImgUrl();
//
//        // 프로필 이미지가 없거나 기본 이미지가 아닌 경우, 기존에 있던 프로필 사진은 삭제한다.
//        if(!isDefaultProfileImgOrNullOrOAuth2ProfileImg(staleProfileImgUrl)){
//            fileService.delete(fileService.getPureFilePath(staleProfileImgUrl));
//        }
//
//        FileMetadata profileImgMetadata = fileService.upload(profileImg, profile, FileMetadataType.PROFILE_IMG);
//        String imgUri = fileService.getUrl(profileImgMetadata);
//        profile.setProfileImgUrl(imgUri);
//        profileRepository.save(profile);
//        return imgUri;
//    }
    
    
    
}
