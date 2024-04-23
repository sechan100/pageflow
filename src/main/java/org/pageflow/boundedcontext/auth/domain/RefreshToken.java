package org.pageflow.boundedcontext.auth.domain;

import lombok.Value;
import org.pageflow.global.property.PropsAware;

import java.time.Duration;
import java.time.Instant;

@Value
public final class RefreshToken {
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = PropsAware.use().auth.refreshTokenExpireDays;

    private final Instant iat;
    private final Instant exp;


    /**
     * @param sessionId 세션의 고유 식별자
     * @param role 사용자의 권한 수준
     * @return RefreshToken
     * @apiNote Session은 반드시 하나의 RefreshToken만을 가져야한다.
     */
    static RefreshToken issue(){
        Instant now = Instant.now();
        return new RefreshToken(
            now,
            now.plus(Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS))
        );
    }


    public boolean isExpired() {
        return exp.isBefore(Instant.now());
    }
}