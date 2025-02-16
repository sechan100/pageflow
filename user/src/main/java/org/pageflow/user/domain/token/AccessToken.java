package org.pageflow.user.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.vavr.control.Try;
import lombok.Getter;
import org.pageflow.common.property.PropsAware;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.entity.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Getter
public final class AccessToken {

  // jwt 설정 정보 로드
  private static final JwtSignKey SIGN_KEY = new JwtSignKey();
  private static final int ACCESS_TOKEN_EXPIRE_MINUTES = PropsAware.use().auth.accessTokenExpireMinutes;
  public static final String ROLE_CLAIM_KEY = "rol";
  public static final String SESSION_ID_CLAIM_KEY = "sid";

  private final UID uid;
  private final UUID sessionId;
  private final RoleType role;
  private final Instant iat;
  private final Instant exp;


  public AccessToken(
    UUID sessionId,
    UID uid,
    RoleType role,
    Instant iat,
    Instant exp
  ) {
    this.sessionId = sessionId;
    this.uid = uid;
    this.role = role;
    this.iat = iat;
    this.exp = exp;
  }

  /**
   * Session에서 AccessToken을 발급할 때 사용한다.
   * @param sessionId 세션 식별자
   * @param uid 사용자 식별자
   * @param role 사용자 권한
   * @return AccessToken 객체
   */
  public static AccessToken issue(Session session) {
    Instant iat = Instant.now();
    Instant exp = iat.plus(Duration.ofMinutes(AccessToken.ACCESS_TOKEN_EXPIRE_MINUTES));
    return new AccessToken(
      session.getId(),
      new UID(session.getAccount().getId()),
      session.getRole(),
      iat,
      exp
    );
  }

  /**
   * compact된 token으로부터 AccessToken 객체를 생성한다.
   * @param compact
   * @return
   */
  public static AccessToken parse(String compact) {
    Try<Claims> tryParse = Try.of(() ->
      Jwts.parserBuilder()
        .setSigningKey(SIGN_KEY.getSignKey())
        .build()
        .parseClaimsJws(compact)
        .getBody()
    );

    Claims claims = tryParse
      .recover(ExpiredJwtException.class, e -> {
        throw new ProcessResultException(UserCode.ACCESS_TOKEN_EXPIRED);
      })
      .get();

    UUID sessionId = UUID.fromString(claims.get(SESSION_ID_CLAIM_KEY, String.class));
    UID uid = UID.from(claims.getSubject());
    Instant iat = claims.getIssuedAt().toInstant();
    Instant exp = claims.getExpiration().toInstant();
    RoleType role = RoleType.valueOf(claims.get(ROLE_CLAIM_KEY, String.class));

    return new AccessToken(sessionId, uid, role, iat, exp);
  }

  /**
   * 토큰을 compact하여 반환한다.
   * @return
   */
  public String compact() {
    Claims clms = Jwts.claims();

    clms.setSubject(uid.toString()) // "sub": "DKG2325EDdgG"
      .setIssuedAt(Date.from(this.iat))      // "iat": 1516239022
      .setExpiration(Date.from(this.exp));    // "exp": 1516239022
    clms.put(SESSION_ID_CLAIM_KEY, sessionId.toString()); // "sid": "DKG2325EDdgG"
    clms.put(ROLE_CLAIM_KEY, role.name()); // "rol": "ROLE_USER"

    return Jwts.builder()
      .setClaims(clms)
      .signWith(SIGN_KEY.getSignKey())
      .compact();
  }

}