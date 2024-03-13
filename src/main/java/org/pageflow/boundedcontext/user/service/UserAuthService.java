package org.pageflow.boundedcontext.user.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.EmailVerificationRequest;
import org.pageflow.boundedcontext.user.entity.RefreshToken;
import org.pageflow.boundedcontext.user.model.token.AccessToken;
import org.pageflow.boundedcontext.user.model.token.AuthTokens;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.pageflow.boundedcontext.user.repository.EmailVerificationRequestRepository;
import org.pageflow.boundedcontext.user.repository.RefreshTokenRepository;
import org.pageflow.global.api.BizException;
import org.pageflow.global.api.code.UserCode;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.infra.email.EmailRequest;
import org.pageflow.infra.email.EmailSender;
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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {
    
    private final CustomProps props;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationProvider authenticationProvider;
    private final EmailVerificationRequestRepository emailVerificationRequestRepository;
    private final EmailSender emailSender;
    private final UtilityUserService userUtil;
    private final JwtProvider jwtProvider;
    private static final String EMAIL_VERIFICATION_URI = "/email/verify";
    
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
                RefreshToken refreshToken = refreshTokenRepository.save(
                        RefreshToken.builder()
                                .id(refreshTokenUUID) // UUID
                                .expiredAt(System.currentTimeMillis() + (props.site().refreshTokenExpireDays() * MilliSeconds.DAY)) // 만료시간
                                // UID와 연관관계 매핑(프록시로만 조회하여 굳이 사용하지 않을 Account를 쿼리하지 않고 id로만 매핑)
                                .account(accountRepository.getReferenceById(UID))
                                .build()
                );
                return new AuthTokens(accessToken, refreshToken); // RETURN
            } catch(Exception e) {
                throw new JJabException("refresh accessToken 영속화 실패", e);
            }
        }
        
    public void sendEmailVerificationMail(String unVerifiedEmail){
        // authorization_code 생성 -> 요청보안
        String authorizationCode; // UUID
            
        // 이미 요청정보가 존재하는 경우 -> 기존의 authorizationCode 사용
        if(emailVerificationRequestRepository.existsById(unVerifiedEmail)){
            authorizationCode = emailVerificationRequestRepository
                    .findById(unVerifiedEmail)
                    .orElseThrow()
                    .getAuthorizationCode();
        } else {
            authorizationCode = UUID.randomUUID().toString();
        }
        
        // 이메일 인증 요청 정보 저장
        EmailVerificationRequest requestCache = EmailVerificationRequest.builder()
                .email(unVerifiedEmail)
                .authorizationCode(authorizationCode)
                .build();
        emailVerificationRequestRepository.save(requestCache);
        
        // 이메일 요청 객체 구성
        Map<String, Object> models = Map.of(
                "email", unVerifiedEmail,
                "authorizationCode", authorizationCode,
                "verificationUri", EMAIL_VERIFICATION_URI,
                "serverHost", props.site().baseUrl()
        );
        EmailRequest request = EmailRequest.builder()
                .from(props.email().from().noReply())
                .fromName("Pageflow")
                .to(unVerifiedEmail)
                .subject("[Pageflow] 이메일 인증 링크")
                .template("/email-verification")
                .models(models)
                .build();
        
        // 이메일 발송
        emailSender.sendEmail(request);
    }
    
}
