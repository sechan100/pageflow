package org.pageflow.user.dto.token;

import lombok.Value;
import org.pageflow.user.domain.entity.Session;

import java.time.Instant;
import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class RefreshTokenDto {
  UUID sessionId;
  Instant exp;

  public static RefreshTokenDto from(Session session) {
    return new RefreshTokenDto(
      session.getId(),
      Instant.ofEpochMilli(session.getRefreshToken().getExp())
    );
  }
}
