package org.pageflow.boundedcontext.auth.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.vavr.control.Try;
import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.domain.UID;
import org.pageflow.global.api.code.Code1;
import org.pageflow.global.property.PropsAware;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * VO
 */
@Value
public final class AccessToken {

    // jwt 설정 정보 로드
    private static final JwtProps JWT_PROPS = new JwtProps();
    public static final String ROLE_CLAIM_KEY = "rol";
    public static final String SESSION_ID_CLAIM_KEY = "sid";


    private final UID uid;
    private final SessionId sessionId;
    private final RoleType role;
    private final Instant iat;
    private final Instant exp;


    /**
     * 명시적 factory method인 {@link #issue}를 대신 사용함
     */
    private AccessToken(SessionId sessionId, UID uid, RoleType role){
        this.sessionId = sessionId;
        this.uid = uid;
        this.role = role;
        this.iat = Instant.now();
        this.exp = iat.plus(Duration.ofMinutes(JWT_PROPS.getAccessTokenExpireMinutes()));
    }

    /**
     * compact로부터 토큰 객체를 생성할 때 사용
     */
    private AccessToken(Claims claims){
        this.sessionId = SessionId.from(claims.get(SESSION_ID_CLAIM_KEY, String.class));
        this.uid = UID.from(claims.getSubject());
        this.iat = claims.getIssuedAt().toInstant();
        this.exp = claims.getExpiration().toInstant();
        this.role = RoleType.valueOf(claims.get(ROLE_CLAIM_KEY, String.class));
    }


    /**
     * Session에서 AccessToken을 발급할 때 사용한다.
     * 이를 위한 package-private factory method.
     * @param sessionId 세션 식별자
     * @param uid 사용자 식별자
     * @param role 사용자 권한
     * @return AccessToken 객체
     */
    static AccessToken issue(SessionId sessionId, UID uid, RoleType role){
        return new AccessToken(sessionId, uid, role);
    }

    public static AccessToken parse(String compact){
        Try<Claims> tryParse = Try.of(() ->
            Jwts.parserBuilder()
                .setSigningKey(JWT_PROPS.getSignKey())
                .build()
                .parseClaimsJws(compact)
                .getBody()
        );

        Claims claims = tryParse
            .recover(ExpiredJwtException.class, e -> {throw Code1.ACCESS_TOKEN_EXPIRED.fire();})
            .recover(JwtException.class, e -> {throw Code1.INVALID_ACCESS_TOKEN.fire();})
            .get();

        return new AccessToken(claims);
    }

    public String compact(){
        Claims clms = Jwts.claims();

        clms.setSubject(uid.toString()) // "sub": "DKG2325EDdgG"
            .setIssuedAt(Date.from(this.iat))      // "iat": 1516239022
            .setExpiration(Date.from(this.exp));    // "exp": 1516239022
        clms.put(SESSION_ID_CLAIM_KEY, sessionId.toString()); // "sid": "DKG2325EDdgG"
        clms.put(ROLE_CLAIM_KEY, role.name()); // "rol": "ROLE_USER"

        return Jwts.builder()
            .setClaims(clms)
            .signWith(JWT_PROPS.getSignKey())
            .compact();
    }


    @Value
    private static class JwtProps {
        Key signKey;
        int accessTokenExpireMinutes;

        private JwtProps(){
            byte[] decodedKey = PropsAware.use().auth.jwtSecret
                .getBytes(StandardCharsets.UTF_8);
            this.signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");

            this.accessTokenExpireMinutes = PropsAware.use().auth.accessTokenExpireMinutes;
        }
    }
}