package org.pageflow.boundedcontext.user.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.RefreshToken;
import org.pageflow.boundedcontext.user.model.token.AccessToken;
import org.pageflow.boundedcontext.user.model.token.AuthTokens;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.boundedcontext.user.repository.RefreshTokenRepo;
import org.pageflow.global.api.code.UserCode;
import org.pageflow.global.api.code.exception.BizException;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.util.MilliSeconds;
import org.pageflow.util.exception.JJabException;
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
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {
    
    private final CustomProps props;
    private final AccountRepo accountRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final $UserServiceUtil userUtil;
    private final JwtProvider jwtProvider;
    
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
    
    public AuthTokens createSession(Long UID, RoleType role){
        // AccessToken 발급
        AccessToken accessToken = jwtProvider.generateAccessToken(UID, role);
        
        // refreshToken을 생성(새로 생성된 세션 정보를 기록)
        String refreshTokenUUID = UUID.randomUUID().toString();
        try {
            RefreshToken refreshToken = refreshTokenRepo.save(
                    RefreshToken.builder()
                            .id(refreshTokenUUID) // UUID
                            .expiredAt(System.currentTimeMillis() + (props.site().refreshTokenExpireDays() * MilliSeconds.DAY)) // 만료시간
                            // UID와 연관관계 매핑(프록시로만 조회하여 굳이 사용하지 않을 Account를 쿼리하지 않고 id로만 매핑)
                            .account(accountRepo.getReferenceById(UID))
                            .build()
            );
            return new AuthTokens(accessToken, refreshToken); // RETURN
        } catch(Exception e) {
            throw new JJabException("refresh accessToken 영속화 실패", e);
        }
    }
}
