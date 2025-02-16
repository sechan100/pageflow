package org.pageflow.user.dto.token;

import lombok.Value;
import org.pageflow.user.domain.entity.Session;
import org.pageflow.user.domain.token.AccessToken;

/**
 * @author : sechan
 */
@Value
public class AuthTokens {
  AccessTokenDto accessToken;
  RefreshTokenDto refreshToken;

  public static AuthTokens from(AccessToken accessToken, Session session) {
    return new AuthTokens(
      AccessTokenDto.from(accessToken),
      RefreshTokenDto.from(session)
    );
  }
}
