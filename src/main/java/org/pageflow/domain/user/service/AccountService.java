package org.pageflow.domain.user.service;


import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.domain.user.constants.Role;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.AwaitingVerificationEmailRepository;
import org.pageflow.infra.email.EmailRequest;
import org.pageflow.infra.email.EmailSender;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.repository.FileMetadataRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    @Getter
    private final AccountRepository accountRepository;
    private final AwaitingVerificationEmailRepository emailCacheRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final EmailSender emailSender;
    private final CustomProperties customProperties;
    
    
    
    // OAuth2를 이용하여 받아온 사용자 정보를 Account 엔티티 형태로 converting하여 일반적인 회원가입시에 사용하는 register 메소드로 DB저장을 위임
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
                    .role(Role.USER)
                    .build();
            
            accountRepository.save(account);
    }
    
    
    /**
     * Profile은 같이 업데이트 하지 않음
     */
    @Transactional
    public void updateAccount(AccountDto accountDto) {
            Account account = accountRepository.findByUsername(accountDto.getUsername());
            account.setUsername(accountDto.getUsername());
            account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            account.setProvider(accountDto.getProvider());
            account.setEmail(accountDto.getEmail());
    }
    
    /**
     * send email for verifying email
     * @param toEmail 이메일 인증 요청을 보낼 이메일 주소
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
    
    
    
    
    
    // ****************************************************
    // *********     JPA Repository service      **********
    // ****************************************************
    
    public Account findByUsernameWithProfile(String username) {
        return setProfileImgUrl(accountRepository.findByUsernameWithProfile(username));
    }
    
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }
    
    public boolean existsByEmailAndProvider(String email, String provider) {
        return accountRepository.existsByEmailAndProvider(email, provider);
    }
    
    
    /**
     * Account의 Profile 엔티티의 Transient인 프로필 이미지 URL 필드를 초기화 후 반환
     * @param account DB에서 가져온 Account 엔티티
     * @return 프로필 이미지 URL이 추가된 Account 엔티티
     */
    private Account setProfileImgUrl(Account account) {
        Profile profile = account.getProfile();
        List<FileMetadata> profileImgFileMetadatas = fileMetadataRepository
                .findAllByOwnerIdAndOwnerEntityTypeAndFileMetadataType(
                        profile.getId(), Profile.class.getSimpleName(), FileMetadataType.PROFILE_IMG
                );
        
        String profileImgUrl;
        
        if(profileImgFileMetadatas.isEmpty()) {
            profileImgUrl = "/img/default_profile_img.png";
        } else {
            FileMetadata profileImgFileMetadata = profileImgFileMetadatas.get(0);
            profileImgUrl =
                customProperties.getFiles().getImg().getBaseUrl()   // {baseUrl}
                + profileImgFileMetadata.getPathPrefix()            // {y}/{m}/{d}/
                + profileImgFileMetadata.getManagedFilename();      // {UUID}.{ext}
        }
        
        
        profile.setProfileImgUrl(profileImgUrl);
        
        return account;
    }
}
