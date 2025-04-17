package org.pageflow.user.port.in.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.entity.Session;
import org.pageflow.user.domain.token.AccessToken;
import org.pageflow.user.domain.token.JwtSignKey;
import org.pageflow.user.port.in.TokenUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TokenProvider implements TokenUseCase {
  private final JwtSignKey jwtSignKey;
  private final ApplicationProperties properties;


  @Override
  public Result<AccessToken> parseAccessToken(String accessTokenCompact) {
    Result<AccessToken> result = null;

    try {
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(jwtSignKey.getSignKey())
        .build()
        .parseClaimsJws(accessTokenCompact)
        .getBody();
      UUID sessionId = UUID.fromString(claims.get(AccessToken.SESSION_ID_CLAIM_KEY, String.class));
      UID uid = UID.from(claims.getSubject());
      Instant iat = claims.getIssuedAt().toInstant();
      Instant exp = claims.getExpiration().toInstant();
      RoleType role = RoleType.valueOf(claims.get(AccessToken.ROLE_CLAIM_KEY, String.class));

      AccessToken accessToken = new AccessToken(sessionId, uid, role, iat, exp, jwtSignKey.getSignKey());
      result = Result.ok(accessToken);

      // 만료된 토큰인 경우
    } catch(ExpiredJwtException expiredJwtException) {
      result = Result.of(UserCode.ACCESS_TOKEN_EXPIRED);
    }

    return result;
  }

  /**
   * Session에서 AccessToken을 발급할 때 사용한다.
   *
   * @param session
   * @return AccessToken 객체
   */
  public AccessToken issueAccessToken(Session session) {
    Instant iat = Instant.now();
    Instant exp = iat.plus(Duration.ofMinutes(properties.auth.accessTokenExpireMinutes));
    return new AccessToken(
      session.getId(),
      session.getUser().getUid(),
      session.getRole(),
      iat,
      exp,
      jwtSignKey.getSignKey()
    );
  }

}
