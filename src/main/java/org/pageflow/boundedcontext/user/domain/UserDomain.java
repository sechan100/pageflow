package org.pageflow.boundedcontext.user.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.boundedcontext.user.entity.RefreshToken;
import org.pageflow.boundedcontext.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.boundedcontext.user.model.token.AccessToken;
import org.pageflow.boundedcontext.user.model.token.AuthTokens;
import org.pageflow.boundedcontext.user.model.user.AggregateUser;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.pageflow.boundedcontext.user.repository.ProfileRepo;
import org.pageflow.boundedcontext.user.repository.RefreshTokenRepo;
import org.pageflow.boundedcontext.user.service.$UserServiceUtil;
import org.pageflow.boundedcontext.user.service.UserAuthService;
import org.pageflow.boundedcontext.user.service.UserCommander;
import org.pageflow.global.api.code.SessionCode;
import org.pageflow.global.api.code.exception.BizException;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.entity.DataNotFoundException;
import org.pageflow.infra.extension.stereotype.Domain;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * @author : sechan
 */
@Domain
@Slf4j
@RequiredArgsConstructor
public class UserDomain {
    
    private final CustomProps props;
    private final AccountRepository accountRepository;
    private final ProfileRepo profileRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtProvider jwtProvider;
    private final $UserServiceUtil userUtil;
    private final UserAuthService userAuthService;
    private final UserCommander userCommander;
    
    
    /**
     * 회원가입.
     * form과 OAuth2에 관계없이 SignupForm 객체의 정보를 기반으로 Account와 Profile을 생성한다.
     */
    public AggregateUser signup(SignupForm form, ProviderType provider, RoleType userRole) {
        
        // username 검사
        userUtil.validateUsername(form.getUsername());
        // email 검사
        userUtil.validateEmail(form.getEmail());
        // password 검사
        userUtil.validatePassword(form.getPassword());
        // penname 검사
        userUtil.validatePenname(form.getPenname());
        
        // 프로필 생성
        Profile profile = Profile.builder().penname(form.getPenname())
                // 프로필 사진을 등록하지 않은 경우, 설정값에 저장된 기본 이미지url을 할당함.
                .profileImgUrl(Objects.requireNonNullElse(form.getProfileImgUrl(), props.defaults().userProfileImg())).build();
        
        // 계정 생성
        Account account = Account.builder().provider(provider).email(form.getEmail()).username(form.getUsername()).password(passwordEncoder.encode(form.getPassword())).role(userRole).build();
        
        return userCommander.saveUser(account, profile);
    }
    
    
    /**
     * form 로그인 처리
     * 새로운 세션을 생성하고, access, refresh 토큰을 반환한다.
     * @return 세션의 인증 토큰 객체
     */
    public AuthTokens formLogin(String username, String password) {
        Authentication authentication = userAuthService.authenticate(username, password);
        
        if(authentication.getPrincipal() instanceof InitialAuthenticationPrincipal principal) {
            return userAuthService.createSession(principal.getUID(), principal.getRole());
        } else {
            throw new IllegalArgumentException("authentication.getPrincipal() 객체가 PrincipalContext의 인스턴스가 아닙니다. \n " + "UserDetailsService의 구현이 올바르지 않을 수 있습니다.");
        }
    }
    
    
    /**
     * 이미 {@link org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter}에서 모든 로그인 과정이 처리된 이후에,
     * 단지 session을 생성하고 토큰을 발급하기 위해서 위임되는 메소드. 복잡한 인증 과정을 거치지 않고, 단순히 세션을 생성하고 인증 토큰을 반환한다.
     * @param username OAuth2로 로그인하는 사용자의 username
     * @return
     */
    public AuthTokens oauth2Login(String username) {
        // OAuth2로 로그인하는 사용자의 UID를 조회
        Long UID = accountRepository.findByUsername(username).getUID();
        
        // 세션을 생성 후 반환
        return userAuthService.createSession(UID, RoleType.ROLE_USER);
    }
    
    
    public void logout(String refreshTokenId) {
        refreshTokenRepo.deleteById(refreshTokenId);
    }
    
    
    /**
     * @throws BizException SESSION_EXPIRED
     */
    public AccessToken refresh(String refreshTokenId){
        try {
            // 세션을 조회하고, refreshToken의 만료여부를 확인
            RefreshToken refreshToken = refreshTokenRepo.findWithAccountById(refreshTokenId);
            if(refreshToken.isExpired()) {
                throw new BizException(SessionCode.SESSION_EXPIRED);
            }
            
            // 새 토큰을 발급
            Account user = refreshToken.getAccount();
            return jwtProvider.generateAccessToken(user.getUID(), user.getRole());
            
        // 세션을 찾지 못함
        } catch(DataNotFoundException sessionNotExist) {
            throw new BizException(SessionCode.SESSION_EXPIRED);
        }
    }
}
