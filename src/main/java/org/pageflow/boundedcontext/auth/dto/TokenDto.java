package org.pageflow.boundedcontext.auth.dto;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class TokenDto {

  @Value
  public static class AccessToken {
    String compact;
    Instant exp;
  }

  @Value
  public static class RefreshToken {
    UUID sessionId;
    Instant exp;
  }


  @Value
  public static class AuthTokens {
    AccessToken accessToken;
    RefreshToken refreshToken;
  }
}
