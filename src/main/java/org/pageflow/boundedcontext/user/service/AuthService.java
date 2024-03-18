package org.pageflow.boundedcontext.user.service;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.entity.EmailVerificationRequest;
import org.pageflow.boundedcontext.user.entity.RefreshToken;
import org.pageflow.boundedcontext.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.boundedcontext.user.model.token.AccessToken;
import org.pageflow.boundedcontext.user.model.token.AuthTokens;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.boundedcontext.user.repository.EmailVerificationRequestRepo;
import org.pageflow.boundedcontext.user.repository.RefreshTokenRepo;
import org.pageflow.global.api.BizException;
import org.pageflow.global.api.code.SessionCode;
import org.pageflow.global.api.code.UserCode;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.shared.query.TryQuery;
import org.pageflow.infra.email.EmailRequest;
import org.pageflow.infra.email.EmailSender;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.shared.JJamException;
import org.pageflow.shared.TimeIntorducer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final CustomProps props;
    private final AccountRepo accountRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final EmailVerificationRequestRepo emailVfyReqRepo;
    private final AuthenticationProvider authenticationProvider;
    private final EmailSender emailSender;
    private final UtilityUserService userUtil;
    private final JwtProvider jwtProvider;
    
    public static final String EMAIL_VERIFICATION_URI = "/email/verify";


    /**
     * form 로그인 처리
     * 새로운 세션을 생성하고, access, refresh 토큰을 반환한다.
     *
     * @return 세션의 인증 토큰 객체
     */
    public AuthTokens formLogin(String username, String password){
        Authentication authentication = authenticate(username, password);

        if(authentication.getPrincipal() instanceof InitialAuthenticationPrincipal principal){
            return createSession(principal.getUID(), principal.getRole());
        } else {
            throw new IllegalArgumentException(
                "authentication.getPrincipal() 객체가 PrincipalContext의 인스턴스가 아닙니다. UserDetailsService의 구현체 확인요망");
        }
    }


    /**
     * 이미 {@link org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter}에서 모든 로그인 과정이 처리된 이후에,
     * 단지 session을 생성하고 토큰을 발급하기 위해서 위임되는 메소드. 복잡한 인증 과정을 거치지 않고, 단순히 세션을 생성하고 인증 토큰을 반환한다.
     *
     * @param username OAuth2로 로그인하는 사용자의 username
     */
    public AuthTokens oauth2Login(String username){
        // OAuth2로 로그인하는 사용자의 UID를 조회
        Long UID = accountRepo.findByUsername(username).getUid();

        // 세션을 생성 후 반환
        return createSession(UID, RoleType.ROLE_USER);
    }

    public void logout(String refreshTokenId){
        refreshTokenRepo.deleteById(refreshTokenId);
    }

    /**
     * @throws BizException USER_NOT_FOUND, PASSWORD_NOT_MATCH
     * @throws AuthenticationException UsernameNotFoundException, BadCredentialsException이 아닌 인증 예외
     */
    public Authentication authenticate(String username, String password) {
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
                if(authException instanceof UsernameNotFoundException) {
                    throw UserCode.USER_NOT_FOUND.fireWithData(username);
                // credentials 불일치
                } else if(authException instanceof BadCredentialsException) {
                    throw UserCode.PASSWORD_NOT_MATCH.fire();
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
                        .expiredAt( // 만료시간
                            System.currentTimeMillis() + (props.site().refreshTokenExpireDays() * TimeIntorducer.MilliSeconds.DAY)
                        )
                        // UID와 연관관계 매핑(프록시로만 조회하여 굳이 사용하지 않을 Account를 쿼리하지 않고 id로만 매핑)
                        .account(accountRepo.getReferenceById(UID))
                        .build()
                );
                return new AuthTokens(accessToken, refreshToken); // RETURN
            } catch(RuntimeException e) {
                throw new JJamException("refresh accessToken 영속화 실패", e);
            }
        }

    /**
     * @throws BizException SESSION_EXPIRED
     */
    public AccessToken refresh(String refreshTokenId){
        Try<RefreshToken> find = Try.of(() -> refreshTokenRepo.findWithAccountById(refreshTokenId));

        // 세션을 찾지 못한 경우 -> 이미 만료된 세션이라 서버 스케쥴링으로 지워졌을 수 있음
        RefreshToken refreshToken = find.
            // 찾았는데 만료됐으면 fire
                onSuccess(token -> {
                if(token.isExpired()){
                    throw SessionCode.SESSION_EXPIRED.fire();
                }
            }) // 못찾으면 fire
            .getOrElseThrow(t -> SessionCode.SESSION_EXPIRED.fire());

        // 새 토큰을 발급
        Account user = refreshToken.getAccount();
        return jwtProvider.generateAccessToken(user.getUid(), user.getRole());
    }
    
    /**
     * 만료시간이내에 이미 요청한 기록이 있는 경우, 기존의 인증코드를 재사용함
     */
    @Transactional
    public void sendEmailVerificationMail(Long UID, String unVerifiedEmail) {
        // case 1) 해당 UID를 가진 사용자가 이미 이메일 인증요청을 보낸 기록이 존재하는 경우 가져옴
        TryQuery<EmailVerificationRequest> findById = TryQuery.of(() -> emailVfyReqRepo.findById(UID).get());
        // case 2) 인증요청 기록이 없는 경우 새로운 인증요청을 생성
        EmailVerificationRequest request = findById.findOrElse(
            EmailVerificationRequest.builder()
                .uid(UID)
                .email(unVerifiedEmail)
                .authorizationCode(UUID.randomUUID().toString())
                .build()
        );
        request.setEmail(unVerifiedEmail); // 이메일 재지정(다른 이메일로 인증요청을 보냈을 수도 있음)
        
        // 인증요청 기록 저장
        emailVfyReqRepo.save(request);
        
        // 이메일 요청 객체 구성
        EmailRequest emailRequest = EmailRequest.builder()
                .from(props.email().from().noReply())
                .fromName("Pageflow")
                .to(unVerifiedEmail)
                .subject("[Pageflow] 이메일 인증 링크")
                .template("/email-verification")
                .models(Map.of(
                    "UID", UID,
                    "email", unVerifiedEmail,
                    "authorizationCode", request.getAuthorizationCode(),
                    "verificationUri", EMAIL_VERIFICATION_URI,
                    "serverHost", props.site().baseUrl()
                ))
                .build();
        // 이메일 발송
        emailSender.sendEmail(emailRequest);
    }
    
    /**
     * @ApiCode ALREADY_VERIFIED_EMAIL - 이미 인증된 이메일인 경우
     */
    @Transactional
    public void verifyEmail(Long UID, String email, String code) {
        TryQuery<EmailVerificationRequest> findById = TryQuery.of(() -> emailVfyReqRepo.findById(UID).get());
        
        // 캐쉬 데이터가 없음 -> 인증요청이 만료되었거나 에초에 전송된 요청이 없는 경우 -> 그냥 만료된 것으로 처리
        EmailVerificationRequest cachedRequest = findById
            .findOrElseThrow(UserCode.EXPIRED_EMAIL_VERIFICATION_REQUEST::fire);
        
        // 인증코드 검증
        boolean isCodeMatched = cachedRequest.getAuthorizationCode().equals(code);
        UserCode.INVALID_EMAIL_VERIFICATION_REQUEST
            .predicate(!isCodeMatched,"인증코드가 일치하지 않음");
        
        // 이메일 검증
        boolean isEmailMatched = cachedRequest.getEmail().equals(email);
        UserCode.INVALID_EMAIL_VERIFICATION_REQUEST
            .predicate(!isEmailMatched,"인증요청된 이메일이 일치하지 않음");
        
        // 인증 처리
        Account user = accountRepo.findById(UID).get();
        if(user.isEmailVerified()){
            throw UserCode.ALREADY_VERIFIED_EMAIL.fire();
        } else {
            user.verifyEmail();
        }
        // 이메일 인증요청 삭제
        emailVfyReqRepo.delete(cachedRequest);
    }
}
