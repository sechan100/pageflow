package org.pageflow.infra.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.infra.jwt.token.AccessToken;
import org.pageflow.infra.jwt.token.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.UUID;


@Service
@Slf4j
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    private Key signKey;
    public static final String ROLE_CLAIM_KEY = "rol";
    public static final String UID_CLAIM_KEY = "uid";
    public static final long ACCESS_TOKEN_EXPIRED_IN = (1000 * 60) * 30; // 30분
    public static final long REFRESH_TOKEN_EXPIRED_IN = (1000 * 60) * 60 * 24 * 14; // 14일
    
    
    public AccessToken generateAccessToken(Long userId, String username, RoleType roleType) {
        Assert.notNull(userId, "userId must not be null");
        Assert.hasText(username, "username must not be null or empty");
        Assert.notNull(roleType, "roleType must not be null");
        
        Date issuedAt = new Date();
        Date expiredIn = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_IN);
        
        // 클래임 작성
        Claims claims = Jwts.claims();
        claims.setSubject(username); // "sub": "username"
        claims.put(UID_CLAIM_KEY, userId); // "uid": 1
        claims.put(ROLE_CLAIM_KEY, roleType); // "rol": "ROLE_USER"
        claims.setIssuedAt(issuedAt); // "iat": 1516239022
        claims.setExpiration(expiredIn); // "exp": 1516239022
        
        // 토큰 dto 생성
        return AccessToken.builder()
                .token(compact(claims)) // 토큰 인코딩
                .iat(issuedAt)
                .exp(expiredIn)
                .UID(userId)
                .role(roleType)
                .username(username)
                .build();
    }
    
    public RefreshToken generateRefreshToken(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        
        Date issuedAt = new Date();
        Date expiredIn = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_IN);
        
        // Token Session에 사용할 Id 값인 UUID를 생성
        String sessionId = UUID.randomUUID().toString();
        
        // 클래임 작성
        Claims claims = Jwts.claims();
        claims.setSubject(sessionId); // "subject": "8a5ff68f-e9f8-49da-899a-8222aedc69b8"
        claims.put(UID_CLAIM_KEY, userId); // "uid": 1
        claims.setIssuedAt(issuedAt); // "iat": 1516239022
        claims.setExpiration(expiredIn); // "exp": 1516239022
        
        // refresh token dto 생성
        return RefreshToken.builder()
                .token(compact(claims))
                .iat(issuedAt)
                .exp(expiredIn)
                .UID(userId)
                .sessionId(sessionId)
                .build();
    }
    
    public AccessToken parseAccessToken(String accessToken) {
        Assert.hasText(accessToken, "accessToken must not be null or empty");
        
        Claims claims = parseToken(accessToken);
        return AccessToken.builder()
                .token(accessToken)
                .iat(claims.getIssuedAt())
                .exp(claims.getExpiration())
                .UID(claims.get(UID_CLAIM_KEY, Long.class))
                .role(claims.get(ROLE_CLAIM_KEY, RoleType.class))
                .username(claims.getSubject())
                .build();
    }

    public RefreshToken parseRefreshToken(String refreshToken) {
        Assert.hasText(refreshToken, "refreshToken must not be null or empty");
        
        Claims claims = parseToken(refreshToken);
        return RefreshToken.builder()
                .token(refreshToken)
                .iat(claims.getIssuedAt())
                .exp(claims.getExpiration())
                .UID(claims.get(UID_CLAIM_KEY, Long.class))
                .sessionId(claims.getSubject())
                .build();
    }
    
    public Key getSignKey() {
        if(signKey == null){
            byte[] decodedKey = secretKey.getBytes();
            signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        }
        return signKey;
    }
    
    /**
     * @param token 토큰
     * @return 클레임
     * @throws ExpiredJwtException 토큰이 만료된 경우
     * @throws JwtException 그 외의 파싱 예외
     */
    private Claims parseToken(String token) throws ExpiredJwtException, JwtException {
        try {
            return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
            
        // ExpiredJwtException은 그대로 던짐
        } catch(ExpiredJwtException e) {
            throw e;
            
        // 그 외의 예외는 JwtException으로 던짐
        } catch(Exception exception) {
            log.error("토큰 파싱 실패: {} ", exception.getMessage());
            throw new JwtException(exception.getMessage(), exception);
        }
    }
    
    private String compact(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSignKey())
                .compact();
    }
    
}
