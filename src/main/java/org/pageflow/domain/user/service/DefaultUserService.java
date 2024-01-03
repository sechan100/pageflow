package org.pageflow.domain.user.service;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProps;
import org.pageflow.base.exception.UserFeedbackException;
import org.pageflow.base.exception.code.UserErrorCode;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.jwt.JwtProvider;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.model.token.AccessToken;
import org.pageflow.domain.user.model.token.RefreshToken;
import org.pageflow.domain.user.model.token.SessionToken;
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
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
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
     * @param form 회원가입 폼
     * @param userRole 사용자에게 부여할 권한
     * @return 영속화된 계정
     */
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
                .profileImgUrl(Objects.requireNonNullElse(form.getProfileImgUrl(), customProps.getDefaults().getDefaultUserProfileImg()))
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
            throw new UserFeedbackException(UserErrorCode.BLANK_USERNAME);
        }
        
        // 2. username 정규식 검사
        if(!username.matches(UserSignupPolicy.USERNAME_REGEX)) {
            throw new UserFeedbackException(UserErrorCode.USERNAME_REGEX_NOT_MATCH);
        }
        
        // 3. 사용할 수 없는 username 검사
        for(String invalidUsername : UserSignupPolicy.INVALID_USERNAME) {
            if(username.contains(invalidUsername)) {
                throw new UserFeedbackException(UserErrorCode.UNUSEABLE_USERNAME, username);
            }
        }
        
        // 4. username 중복 검사
        if(accountRepository.existsByUsername(username)){
            throw new UserFeedbackException(UserErrorCode.DUPLICATE_USERNAME, username);
        }
    }
    
    // email 검사
    public void validateEmail(String email){
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(email)){
            throw new UserFeedbackException(UserErrorCode.BLANK_EMAIL);
        }
        
        // 2. email 형식 검사
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw new UserFeedbackException(UserErrorCode.EMAIL_REGEX_NOT_MATCH);
        }
        
        // 3. email 중복 검사
        if(accountRepository.existsByEmailAndEmailVerified(email, true)){
            throw new UserFeedbackException(UserErrorCode.DUPLICATE_EMAIL, email);
        }
    }
    
    // password 검사
    public void validatePassword(String password, @Nullable String passwordConfirm) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(password)){
            throw new UserFeedbackException(UserErrorCode.BLANK_PASSWORD);
        }
        
        // 2. password 정규식 검사
        if(!password.matches(UserSignupPolicy.PASSWORD_REGEX)) {
            throw new UserFeedbackException(UserErrorCode.PASSWORD_REGEX_NOT_MATCH);
        }
        
        // 3. password와 passwordConfirm이 일치 검사
        if(passwordConfirm != null && !password.equals(passwordConfirm)){
            throw new UserFeedbackException(UserErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }
    }
    
    // penname 검사
    public void validatePenname(String penname) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(penname)){
            throw new UserFeedbackException(UserErrorCode.BLANK_PENNAME);
        }
        
        // 2. penname 정규식 검사
        if(!penname.matches(UserSignupPolicy.PENNAME_REGEX)) {
            throw new UserFeedbackException(UserErrorCode.PENNAME_REGEX_NOT_MATCH);
        }
        
        // 3. 사용할 수 없는 필명
        for(String invalidPenname : UserSignupPolicy.INVALID_PENNAME) {
            if(penname.contains(invalidPenname)) {
                throw new UserFeedbackException(UserErrorCode.UNUSEABLE_PENNAME, penname);
            }
        }
        
        // 4. penname 중복 검사
        if(profileRepository.existsByPenname(penname)){
            throw new UserFeedbackException(UserErrorCode.DUPLICATE_PENNAME, penname);
        }
    }
    
    /**
     * @param username 아이디
     * @param password 비밀번호
     * @return accessToken, refreshToken
     */
    public Map<String, SessionToken> login(String username, String password) {
        Authentication authentication = delegateAuthenticate(username, password);
        
        if(authentication.getPrincipal() instanceof PrincipalContext principal) {
            // accessToken 발급
            AccessToken accessToken = jwtProvider.generateAccessToken(
                    principal.getId(),
                    principal.getUsername(),
                    principal.getRole()
            );
            
            // refreshToken 발급
            RefreshToken refreshToken = jwtProvider.generateRefreshToken(principal.getId());
            
            // 응답 객체 반환
            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );
            
        } else {
            throw new IllegalArgumentException("authentication.getPrincipal() 객체가 PrincipalContext의 인스턴스가 아닙니다. \n UserDetailsService의 구현이 올바른지 확인해주세요.");
        }
        
    }
    
    private Authentication delegateAuthenticate(String username, String password) {
        
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
                throw new UserFeedbackException(UserErrorCode.USERNAME_NOT_EXIST, username);
                
            // BadCredentialsException
            } else if (authException instanceof BadCredentialsException) {
                throw new UserFeedbackException(UserErrorCode.PASSWORD_NOT_MATCH);
                
            } else {
                throw authException;
            }
        }
    }
    
    public void logout(String refreshToken) {
        try {
            RefreshToken token = jwtProvider.parseRefreshToken(refreshToken);
            tokenSessionRepository.deleteById(token.getSessionId());
        } catch(ExpiredJwtException ignored) {
            // 어차피 만료된 토큰이면 로그아웃된거나 마찬가지.. -> 무시
        }
    }
    
    public AccessToken refresh(String refreshToken){
        
        try {
            // 파싱된 토큰에서 해당 세션에 해당하는 UUID 형태의 PK를 추출
            String sessionId = jwtProvider.parseRefreshToken(refreshToken).getSessionId();
            
            // 세션의 소유자를 조회
            Account user = tokenSessionRepository.findWithAccountById(sessionId).getAccount();
            
            // 새 토큰을 발급
            return jwtProvider.generateAccessToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
        } catch(ExpiredJwtException e) {
            // 만료된 세션인 경우 피드백
            throw new UserFeedbackException(UserErrorCode.SESSION_EXPIRED);
        }
    }
}
