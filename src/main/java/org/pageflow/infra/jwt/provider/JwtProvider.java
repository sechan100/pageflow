package org.pageflow.infra.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.exception.business.code.SessionCode;
import org.pageflow.global.exception.business.exception.BizException;
import org.pageflow.infra.jwt.dto.AccessTokenDto;
import org.pageflow.infra.jwt.token.AccessToken;
import org.pageflow.util.MilliSeconds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    private Key signKey;
    private final CustomProps props;
    public static final String ROLE_CLAIM_KEY = "rol";
    
    public AccessTokenDto generateAccessToken(Long UID, RoleType role) {
        Assert.notNull(UID, "UID must not be null");
        Assert.notNull(role, "role must not be null");
        
        Date issuedAt = new Date();
        Date expiredAt = new Date(System.currentTimeMillis() + (MilliSeconds.MINUTE * props.site().accessTokenExpireMinutes()));

        // 클래임 작성
        Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(UID)); // "sub": "76499"
        claims.put(ROLE_CLAIM_KEY, role); // "rol": "ROLE_USER"
        claims.setIssuedAt(issuedAt); // "iat": 1516239022
        claims.setExpiration(expiredAt); // "exp": 1516239022
        
        // return
        return AccessTokenDto.builder()
                .accessToken(compact(claims))
                .expiredAt(expiredAt.getTime())
                .build();
    }
    
    public AccessToken parseAccessToken(String accessToken) {
        Assert.hasText(accessToken, "accessToken must not be null or empty");
        
        Claims claims = parseToken(accessToken);
        return AccessToken.builder()
                .token(accessToken)
                .UID(Long.valueOf(claims.getSubject()))
                .iat(claims.getIssuedAt())
                .exp(claims.getExpiration())
                .role(RoleType.valueOf(claims.get(ROLE_CLAIM_KEY).toString()))
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
     * @throws BizException SESSION_EXPIRED, INVALID_TOKEN
     */
    private Claims parseToken(String token){
        try {
            return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
            
        // ExpiredJwtException -> 세션 만료
        } catch(ExpiredJwtException e) {
            throw new BizException(SessionCode.SESSION_EXPIRED);
            
        // 그 외에는 토큰 파싱 실패로 간주
        } catch(Exception exception) {
            log.error("토큰 파싱 실패: {} ", exception.getMessage());
            throw new BizException(SessionCode.INVALID_TOKEN);
        }
    }
    
    private String compact(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSignKey())
                .compact();
    }
    
}
