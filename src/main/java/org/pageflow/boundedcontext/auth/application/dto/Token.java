package org.pageflow.boundedcontext.auth.application.dto;

import lombok.Value;
import org.pageflow.boundedcontext.auth.domain.SessionId;

import java.time.Instant;

/**
 * @author : sechan
 */
public abstract class Token {

    @Value
    public static class AccessTokenDto {
        String compact;
        Instant exp;
    }

    @Value
    public static class RefreshTokenDto {
        SessionId sessionId;
        Instant exp;
    }


    @Value
    public static class AuthTokens {
        AccessTokenDto accessToken;
        RefreshTokenDto refreshToken;
    }
}
