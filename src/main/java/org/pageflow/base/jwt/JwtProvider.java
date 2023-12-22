package org.pageflow.base.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;


@Service
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    private Key signKey;
    private static final String AUTHORITY_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_PERIOD = (1000 * 60) * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_PEROID = (1000 * 60) * 60 * 24 * 14; // 14일
    
    
    

    public String generateAccessToken(Authentication authentication) {
        
        String authority = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toString();
        
        return Jwts.builder()
                .setSubject(authentication.getName()) // "sub": "username"
                .setClaims(Map.of(AUTHORITY_KEY, authority)) // "auth": "ROLE_USER"
                .setIssuedAt(new Date()) // "iat": 1516239022
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_PERIOD)) // "exp": 1516239022
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_PEROID))
                .signWith(getSignKey())
                .compact();
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰의 유효성을 검사
     * @throws ExpiredJwtException 토큰이 만료된 경우
     * @return 유효한 토큰인 경우 true, 그 외의 경우 false
     * */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;

        // 토큰 만료시 예외를 받아서 다시 던짐
        } catch (ExpiredJwtException e) {
            throw e;

        // 그 외의 예외는 false 반환
        } catch (Exception e) {
            return false;
        }
    }
    
    
    public Key getSignKey() {
        if(signKey == null){
            byte[] decodedKey = secretKey.getBytes();
            signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        }
        return signKey;
    }


    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }
}
