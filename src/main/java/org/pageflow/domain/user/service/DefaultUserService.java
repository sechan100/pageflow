package org.pageflow.domain.user.service;


import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.constants.ProfileFetchDepth;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.domain.user.repository.RefreshTokenRepository;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.exception.business.code.UserCode;
import org.pageflow.global.exception.business.exception.BizException;
import org.pageflow.infra.email.EmailSender;
import org.pageflow.infra.file.service.FileService;
import org.pageflow.infra.jwt.provider.JwtProvider;
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
    private final RefreshTokenRepository refreshTokenRepository;
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
    
    /**
     * @throws BizException INVALID_USERNAME, DUPLICATED_USERNAME, USERNAME_CONTAINS_FORBIDDEN_WORD
     */
    public void validateUsername(String username) {
        
        // 1. null, 공백문자 검사
        if(!StringUtils.hasText(username)){
            throw BizException.builder()
                    .code(UserCode.INVALID_USERNAME)
                    .message("비어있는 username; null/빈 문자열/공백 문자열")
                    .build();
        }
        
        // 2. username 정규식 검사
        if(!username.matches(UserSignupPolicy.USERNAME_REGEX)) {
            throw BizException.builder()
                    .code(UserCode.INVALID_USERNAME)
                    .message(UserSignupPolicy.USERNAME_REGEX_DISCRIPTION)
                    .build();
        }
        
        // 3. 사용할 수 없는 username 검사
        for(String forbiddenWord : UserSignupPolicy.FORBIDDEN_USERNAME_WORDS) {
            if(username.contains(forbiddenWord)) {
                throw BizException.builder()
                        .code(UserCode.USERNAME_CONTAINS_FORBIDDEN_WORD)
                        .data(forbiddenWord)
                        .build();
            }
        }
        
        // 4. username 중복 검사
        if(accountRepository.existsByUsername(username)){
            throw new BizException(UserCode.DUPLICATED_USERNAME);
        }
    }
    
    /**
     * @throws BizException INVALID_EMAIL, DUPLICATED_EMAIL
     */
    public void validateEmail(String email) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(email)){
            throw BizException.builder()
                    .code(UserCode.INVALID_EMAIL)
                    .message("비어있는 email; null/빈 문자열/공백 문자열")
                    .build();
        }
        
        // 2. email 형식 검사
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw BizException.builder()
                    .code(UserCode.INVALID_EMAIL)
                    .message("email 형식 오류; 정규식 불일치")
                    .build();
        }
        
        // 3. email 중복 검사
        if(accountRepository.existsByEmailAndEmailVerified(email, true)){
            throw new BizException(UserCode.DUPLICATED_EMAIL);
        }
    }
    
    /**
     * @throws BizException INVALID_PASSWORD
     */
    public void validatePassword(String password) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(password)){
            throw BizException.builder()
                    .code(UserCode.INVALID_PASSWORD)
                    .message("비어있는 password; null/빈 문자열/공백 문자열")
                    .build();
        }
        
        // 2. password 정규식 검사
        if(!password.matches(UserSignupPolicy.PASSWORD_REGEX)) {
            throw BizException.builder()
                    .code(UserCode.INVALID_PASSWORD)
                    .message(UserSignupPolicy.PASSWORD_REGEX_DISCRIPTION)
                    .build();
        }
    }
    
    /**
     * @throws BizException INVALID_PENNAME, DUPLICATED_PENNAME, PENNAME_CONTAINS_FORBIDDEN_WORD
     */
    public void validatePenname(String penname) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(penname)){
            throw BizException.builder()
                    .code(UserCode.INVALID_PENNAME)
                    .message("비어있는 penname; null/빈 문자열/공백 문자열")
                    .build();
        }
        
        // 2. penname 정규식 검사
        if(!penname.matches(UserSignupPolicy.PENNAME_REGEX)) {
            throw BizException.builder()
                    .code(UserCode.INVALID_PENNAME)
                    .message(UserSignupPolicy.PENNAME_REGEX_DISCRIPTION)
                    .build();
        }
        
        // 3. 사용할 수 없는 필명
        for(String forbiddenWord : UserSignupPolicy.FORBIDDEN_PENNAME_WORDS) {
            if(penname.contains(forbiddenWord)) {
                throw BizException.builder()
                        .code(UserCode.PENNAME_CONTAINS_FORBIDDEN_WORD)
                        .data(forbiddenWord)
                        .build();
            }
        }
        
        // 4. penname 중복 검사
        if(profileRepository.existsByPenname(penname)){
            throw new BizException(UserCode.DUPLICATED_PENNAME);
        }
    }
    
    /**
     * @throws BizException USER_NOT_FOUND, PASSWORD_NOT_MATCH
     * @throws AuthenticationException UsernameNotFoundException, BadCredentialsException이 아닌 인증 예외
     */
    public Authentication authenticate(String username, String password) {
        
        Assert.hasText(username, "username must not be empty");
        Assert.hasText(password, "password must not be empty");
        
        UserDetails principal = User.builder()
                .username(username)
                .password(password)
                .build();
        
        try {
            return authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(principal, password)
            );
        } catch (AuthenticationException authException) {
            
            // username을 찾지 못함
            if (authException instanceof UsernameNotFoundException) {
                throw BizException.builder()
                        .code(UserCode.USER_NOT_FOUND)
                        .data(username)
                        .build();
                
            // credentials 불일치
            } else if (authException instanceof BadCredentialsException) {
                throw new BizException(UserCode.PASSWORD_NOT_MATCH);
                
            } else {
                throw authException;
            }
        }
    }
    
    /**
     * @param UID UID
     * @param fetchDepth 프로필 조회 깊이 {@link ProfileFetchDepth}
     * @return 지정한 수준까지 초기화된 후, JPA 세션이 닫힌 상태의 Profile 인스턴스
     */
    public Profile fetchProfile(Long UID, ProfileFetchDepth fetchDepth){
        Preconditions.checkNotNull(UID, "UID must not be null");
        Preconditions.checkNotNull(fetchDepth, "fetchDepth must not be null");
        
        return switch (fetchDepth) {
            case PROXY -> profileRepository.getReferenceById(UID);
            case BASIC -> profileRepository.findById(UID).orElseThrow();
            case WITH_ACCOUNT -> profileRepository.findWithAccountById(UID);
        };
    }
    
}
