package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.business.BizException;
import org.pageflow.global.business.UserApiStatusCode;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.domain.user.repository.TokenSessionRepository;
import org.pageflow.infra.email.EmailSender;
import org.pageflow.infra.file.service.FileService;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class DefaultUserService {
    
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final TokenSessionRepository tokenSessionRepository;
    private final TemplateEngine templateEngine;
    private final EmailSender emailSender;
    private final FileService fileService;
    private final CustomProps customProps;
    private final JwtProvider jwtProvider;
    
    /**
     * 서로 연관이 없는 새로운 Account와 Profile의 인스턴스를 적절한 순서로 연관관계를 지은 후 저장한다. <br>
     * 누가 먼저 영속화되냐, account에 profile이 있냐 profile에 account가 있냐에 따라서 에러가 발생할 수 있기 때문
     */
    @Transactional
    public Account saveUser(Account account, Profile profile) {
        
        // Account 먼저 영속
        Account savedAccount = accountRepository.save(account);
        
        // 영속된 account를 영속되지 않은 profile과 연관지음
        profile.associateAccount(savedAccount);
        
        // Profile 영속
        Profile savedProfile = profileRepository.save(profile);
        
        return savedProfile.getAccount();
    }
    
    public void validateUsername(String username) {
        
        // 1. null, 공백문자 검사
        if(!StringUtils.hasText(username)){
            throw new BizException(UserApiStatusCode.BLANK_USERNAME);
        }
        
        // 2. username 정규식 검사
        if(!username.matches(UserSignupPolicy.USERNAME_REGEX)) {
            throw new BizException(UserApiStatusCode.USERNAME_REGEX_NOT_MATCH);
        }
        
        // 3. 사용할 수 없는 username 검사
        for(String invalidUsername : UserSignupPolicy.INVALID_USERNAME) {
            if(username.contains(invalidUsername)) {
                throw new BizException(UserApiStatusCode.UNUSEABLE_USERNAME, username);
            }
        }
        
        // 4. username 중복 검사
        if(accountRepository.existsByUsername(username)){
            throw new BizException(UserApiStatusCode.DUPLICATE_USERNAME, username);
        }
    }
    
    public void validateEmail(String email){
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(email)){
            throw new BizException(UserApiStatusCode.BLANK_EMAIL);
        }
        
        // 2. email 형식 검사
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw new BizException(UserApiStatusCode.EMAIL_REGEX_NOT_MATCH);
        }
        
        // 3. email 중복 검사
        if(accountRepository.existsByEmailAndEmailVerified(email, true)){
            throw new BizException(UserApiStatusCode.DUPLICATE_EMAIL, email);
        }
    }
    
    public void validatePassword(String password, @Nullable String passwordConfirm) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(password)){
            throw new BizException(UserApiStatusCode.BLANK_PASSWORD);
        }
        
        // 2. password 정규식 검사
        if(!password.matches(UserSignupPolicy.PASSWORD_REGEX)) {
            throw new BizException(UserApiStatusCode.PASSWORD_REGEX_NOT_MATCH);
        }
        
        // 3. password와 passwordConfirm이 일치 검사
        if(passwordConfirm != null && !password.equals(passwordConfirm)){
            throw new BizException(UserApiStatusCode.PASSWORD_CONFIRM_NOT_MATCH);
        }
    }
    
    public void validatePenname(String penname) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(penname)){
            throw new BizException(UserApiStatusCode.BLANK_PENNAME);
        }
        
        // 2. penname 정규식 검사
        if(!penname.matches(UserSignupPolicy.PENNAME_REGEX)) {
            throw new BizException(UserApiStatusCode.PENNAME_REGEX_NOT_MATCH);
        }
        
        // 3. 사용할 수 없는 필명
        for(String invalidPenname : UserSignupPolicy.INVALID_PENNAME) {
            if(penname.contains(invalidPenname)) {
                throw new BizException(UserApiStatusCode.UNUSEABLE_PENNAME, penname);
            }
        }
        
        // 4. penname 중복 검사
        if(profileRepository.existsByPenname(penname)){
            throw new BizException(UserApiStatusCode.DUPLICATE_PENNAME, penname);
        }
    }
    
    public Authentication authenticate(String username, String password) {
        
        Assert.hasText(username, "username이 비어있습니다.");
        Assert.hasText(password, "password가 비어있습니다.");
        
        UserDetails principal = User.builder()
                .username(username)
                .password(password)
                .build();
        
        try {
            return authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(principal, password)
            );
        } catch (AuthenticationException authException) {
            
            // UsernameNotFoundException
            if (authException instanceof UsernameNotFoundException) {
                throw new BizException(UserApiStatusCode.USERNAME_NOT_EXIST, username);
                
            // BadCredentialsException
            } else if (authException instanceof BadCredentialsException) {
                throw new BizException(UserApiStatusCode.PASSWORD_NOT_MATCH);
                
            } else {
                throw authException;
            }
        }
    }
    
    
}
