package org.pageflow.boundedcontext.user.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@Builder
@RedisHash(value = "user-email-verification", timeToLive = 60 * 60)
public class EmailVerificationRequest {
    
    @Id
    private String email;
    
    /**
     * UUID
     */
    private String authorizationCode;
    
}
