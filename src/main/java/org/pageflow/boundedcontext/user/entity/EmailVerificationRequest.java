package org.pageflow.boundedcontext.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.global.data.Entity;
import org.pageflow.shared.TimeIntorducer;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@Setter
@Builder
@RedisHash(value = "user-email-verification", timeToLive = TimeIntorducer.Seconds.HOUR)
public class EmailVerificationRequest implements Entity {
    
    @Id
    private Long uid;
    
    private String email;
    
    /**
     * UUID
     */
    private String authorizationCode;
    
    
    
}
