package org.pageflow.user.dto.token;

import lombok.Value;
import org.pageflow.user.domain.token.AccessToken;

import java.time.Instant;

/**
 * @author : sechan
 */
@Value
public class AccessTokenDto {
  String compact;
  Instant exp;

  public static AccessTokenDto from(AccessToken accessToken) {
    return new AccessTokenDto(
      accessToken.compact(),
      accessToken.getExp()
    );
  }
}
