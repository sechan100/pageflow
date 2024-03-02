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
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtProvider jwtProvider;
    private final $UserServiceUtil userUtil;
    private final UserAuthService userAuthService;
    private final UserCommander userCommander;
    
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
    
    public AuthTokens login(String username, String password) {
        Authentication authentication = userAuthService.authenticate(username, password);
        
        if(authentication.getPrincipal() instanceof InitialAuthenticationPrincipal principal) {
            return userAuthService.createSession(principal.getUID(), principal.getRole());
        } else {
            throw new IllegalArgumentException("authentication.getPrincipal() 객체가 PrincipalContext의 인스턴스가 아닙니다. \n " + "UserDetailsService의 구현이 올바르지 않을 수 있습니다.");
        }
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
