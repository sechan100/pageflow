package org.pageflow.domain.user.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.base.constants.CustomProps;
import org.pageflow.base.exception.UserFeedbackException;
import org.pageflow.base.exception.code.UserApiStatusCode;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.entity.TokenSession;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.TokenSessionRepository;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.infra.jwt.token.AccessToken;
import org.pageflow.infra.jwt.token.RefreshToken;
import org.pageflow.infra.jwt.token.SessionToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author : sechan
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserApplicationImpl implements UserApplication {
    
    private final CustomProps customProps;
    private final PasswordEncoder passwordEncoder;
    private final DefaultUserService defaultUserService;
    private final AccountRepository accountRepository;
    private final TokenSessionRepository tokenSessionRepository;
    private final JwtProvider jwtProvider;
    
    @Override
    public Account signup(SignupForm form, ProviderType provider, RoleType userRole) {
        
        // username 검사
        defaultUserService.validateUsername(form.getUsername());
        // email 검사
        defaultUserService.validateEmail(form.getEmail());
        // password 검사
        defaultUserService.validatePassword(form.getPassword(), form.getPasswordConfirm());
        // penname 검사
        defaultUserService.validatePenname(form.getPenname());
        
        // 프로필 생성
        Profile profile = Profile.builder()
                .penname(form.getPenname())
                // 프로필 사진을 등록하지 않은 경우, 설정값에 저장된 기본 이미지url을 할당함.
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
        
        return defaultUserService.saveUser(account, profile);
    }
    
    @Override
    public Map<String, SessionToken> login(String username, String password) {
        Authentication authentication = defaultUserService.authenticate(username, password);
        
        if(authentication.getPrincipal() instanceof PrincipalContext principal) {
            // accessToken 발급
            AccessToken accessToken = jwtProvider.generateAccessToken(
                    principal.getId(),
                    principal.getUsername(),
                    principal.getRole()
            );
            
            // refreshToken 발급
            RefreshToken refreshToken = jwtProvider.generateRefreshToken(principal.getId());
            
            try {
                // 새로운 세션 저장
                tokenSessionRepository.save(
                        TokenSession.builder()
                                .id(refreshToken.getSessionId()) // UUID
                                .refreshToken(refreshToken.getToken()) // refreshToken
                                .expiredIn(refreshToken.getExp().getTime()) // 만료시간
                                .account(accountRepository.getReferenceById(principal.getId())) // account
                                .build()
                );
            } catch(Exception e) {
                // 혹여나 Session UUID가 중복된 경우...
                log.error("refresh token 영속화 실패: {}", e.getMessage());
            }
            
            // 응답 객체 반환
            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );
            
        } else {
            throw new IllegalArgumentException("authentication.getPrincipal() 객체가 PrincipalContext의 인스턴스가 아닙니다. \n UserDetailsService의 구현이 올바른지 확인해주세요.");
        }
        
    }
    
    @Override
    public void logout(String refreshToken) {
        try {
            RefreshToken token = jwtProvider.parseRefreshToken(refreshToken);
            tokenSessionRepository.deleteById(token.getSessionId());
        } catch(ExpiredJwtException ignored) {
            // 어차피 만료된 토큰이면 로그아웃된거나 마찬가지이므로, 해당 예외는 무시한다.
        }
    }
    
    @Override
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
            throw new UserFeedbackException(UserApiStatusCode.SESSION_EXPIRED);
        }
    }
}
