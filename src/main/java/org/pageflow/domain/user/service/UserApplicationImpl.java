package org.pageflow.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.entity.RefreshToken;
import org.pageflow.domain.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.RefreshTokenRepository;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.entity.DataNotFoundException;
import org.pageflow.global.exception.business.code.SessionCode;
import org.pageflow.global.exception.business.exception.BizException;
import org.pageflow.infra.jwt.dto.AccessTokenDto;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.util.MilliSeconds;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * @author : sechan
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserApplicationImpl implements UserApplication {
    
    private final CustomProps props;
    private final PasswordEncoder passwordEncoder;
    private final DefaultUserService defaultUserService;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
                .profileImgUrl(Objects.requireNonNullElse(form.getProfileImgUrl(), props.defaults().userProfileImg()))
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
    public LoginTokens login(String username, String password) {
        Authentication authentication = defaultUserService.authenticate(username, password);
        
        if(authentication.getPrincipal() instanceof InitialAuthenticationPrincipal principal) {
            // accessToken 발급
            AccessTokenDto accessToken = jwtProvider.generateAccessToken(
                    principal.getUID(),
                    principal.getRole()
            );
            
            // refreshToken을 생성(새로 생성된 세션 정보를 기록)
            String refreshTokenId = UUID.randomUUID().toString();
            try {
                refreshTokenRepository.save(
                        RefreshToken.builder()
                                .id(refreshTokenId) // UUID
                                .expiredAt(System.currentTimeMillis() + (props.site().refreshTokenExpireDays() * MilliSeconds.DAY)) // 만료시간
                                // UID와 연관관계 매핑(프록시로만 조회하여 굳이 사용하지 않을 Account를 쿼리하지 않고 id로만 매핑)
                                .account(accountRepository.getReferenceById(principal.getUID()))
                                .build()
                );
            } catch(Exception e) {
                // 혹여나 Session UUID가 중복된 경우...
                log.error("refresh token 영속화 실패: {}", e.getMessage());
            }
            
            // RETURN
            return new LoginTokens(
                    accessToken.accessToken(),
                    accessToken.expiredAt(),
                    refreshTokenId
            );
            
        } else {
            throw new IllegalArgumentException("authentication.getPrincipal() 객체가 PrincipalContext의 인스턴스가 아닙니다. \n " +
                    "UserDetailsService의 구현이 올바르지 않을 수 있습니다.");
        }
    }
    
    @Override
    public void logout(String refreshTokenId) {
        refreshTokenRepository.deleteById(refreshTokenId);
    }
    
    /**
     * @throws BizException SESSION_EXPIRED
     */
    @Override
    public AccessTokenDto refresh(String refreshTokenId){
        try {
            // 세션을 조회하고, refreshToken의 만료여부를 확인
            RefreshToken refreshToken = refreshTokenRepository.findWithAccountById(refreshTokenId);
            if(refreshToken.isExpired()) {
                throw new BizException(SessionCode.SESSION_EXPIRED);
            }
            
            // 새 토큰을 발급
            Account user = refreshToken.getAccount();
            return jwtProvider.generateAccessToken(user.getId(), user.getRole());
            
        // 세션을 찾지 못함
        } catch(DataNotFoundException sessionNotExist) {
            throw new BizException(SessionCode.SESSION_EXPIRED);
        }
    }
}
