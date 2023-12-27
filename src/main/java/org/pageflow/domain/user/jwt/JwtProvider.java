package org.pageflow.domain.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.TokenSession;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.TokenSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
@Slf4j
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    private final TokenSessionRepository tokenSessionRepository;
    private final AccountRepository accountRepository;
    private Key signKey;
    private static final String AUTHORITY_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRED_IN = (1000 * 60) * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRED_IN = (1000 * 60) * 60 * 24 * 14; // 14일
    
    public JwtProvider(
            TokenSessionRepository tokenSessionRepository,
            AccountRepository accountRepository
    ) {
        this.tokenSessionRepository = tokenSessionRepository;
        this.accountRepository = accountRepository;
    }
    
    public TokenDto generateTokenDto(Long userId, Authentication authentication) {
        
        // [[ Access Token 생성
        TokenResult accessTokenRes = generateAccessToken(
                userId,
                authentication.getName(),
                RoleType.getRoleType(authentication.getAuthorities())
        );
        
        String accessToken = accessTokenRes.getToken();
        long accessTokenExpiredIn = accessTokenRes.getExpiredIn();
        // ]]
        
        // [[ Refresh Token 생성
        String refreshToken = generateRefreshToken(userId, authentication.getName()).getToken();
        // ]]
        
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredIn(accessTokenExpiredIn)
                .build();
    }
    
    @Data
    @Builder
    private static class TokenResult {
        private String token;
        private long expiredIn;
    }
    
    /**
     * access token 발급
     */
    public TokenResult generateAccessToken(Long userId, String username, RoleType roleType) {
        
        Date expiredIn = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_IN);
        
        String token = Jwts.builder()
                .setSubject(username) // "sub": "username"
                .addClaims(Map.of("id", userId)) // "id": 34
                .addClaims(Map.of(AUTHORITY_KEY, roleType)) // "auth": "ROLE_USER"
                .setIssuedAt(new Date()) // "iat": 1516239022
                .setExpiration(expiredIn) // "exp": 1516239022
                .signWith(getSignKey())
                .compact();
        
        return TokenResult.builder()
                .token(token)
                .expiredIn(expiredIn.getTime())
                .build();
    }

    /**
     *  refresh token 발급 및 저장
     */
    public TokenResult generateRefreshToken(Long userId, String username) {
        
        Date expiredIn = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_IN);
        
        // refresh token 생성
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiredIn)
                .signWith(getSignKey())
                .compact();
        
        // TokenSession에 refreshToken을 저장(유니크 키: username_expiredIn(UTC))
        String sessionKey = username + "_" +  expiredIn.getTime();
        
        // 저장
        tokenSessionRepository.save(
                TokenSession.builder()
                        .sessionKey(sessionKey)
                        .refreshToken(token)
                        .account(accountRepository.getReferenceById(userId))
                        .build()
        );
        
        
        return TokenResult.builder()
                .token(token)
                .expiredIn(expiredIn.getTime())
                .build();
    }
    
    /**
     * access token을 통해 인증객체를 추출
     * @throws IllegalArgumentException 토큰에 권한 정보가 없는 경우.(refresh token인 경우)
     */
    public Authentication parseAuthentication(String accessToken) {
        
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
        
        // 권한 추출
        Optional<String> authorityOrNull = Optional.ofNullable(claims.get(AUTHORITY_KEY).toString());
        
        // 권한 정보 유무 확인
        if (authorityOrNull.isEmpty()) {
            throw new IllegalArgumentException("권한 정보가 없는 토큰입니다.");
        }
        
        
        // principal 작성
        UserDetails principal = new PrincipalContext(
                claims.get("id", Long.class),
                claims.getSubject(),
                "",
                RoleType.valueOf(authorityOrNull.get())
        );
        
        // 인증 객체 작성 및 반환
        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                RoleType.getAuthorities(authorityOrNull.get())
        );
    }
    

    /**
     * 토큰의 유효성을 검사
     * @throws ExpiredJwtException 토큰이 만료된 경우
     * @return 유효한 토큰인 경우 true, 그 외의 경우 false
     * */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;

        // 토큰 만료시 예외를 받아서 다시 던짐 -> ExpiredJwtException만 따로 처리
        } catch (ExpiredJwtException e) {
            throw e;
        } catch(UnsupportedJwtException unsupportedJwtException){
            log.error("지원되지 않는 토큰입니다. 예외: {} ", unsupportedJwtException.getMessage());
        } catch(SignatureException signatureException){
            log.error("잘못된 토큰 서명입니다. 예외: {} ", signatureException.getMessage());
        } catch(IllegalArgumentException illegalArgumentException){
            log.error("토큰이 null이거나 빈문자열, 또는 공백문자로만 이루어져 있습니다. 예외: {} ", illegalArgumentException.getMessage());
        } catch(Exception exception){
            log.error("알 수 없는 토큰 예외. 예외: {} ", exception.getMessage());
        }
        
        // 예외 상황은 모두 false 반환
        return false;
    }
    
    
    public Key getSignKey() {
        if(signKey == null){
            byte[] decodedKey = secretKey.getBytes();
            signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        }
        return signKey;
    }


    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }
}
