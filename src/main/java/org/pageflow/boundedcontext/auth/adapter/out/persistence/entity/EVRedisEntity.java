package org.pageflow.boundedcontext.auth.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


/**
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("email-verification-request")
public class EVRedisEntity {
    @Id
    private Long uid;
    private String authCode; // uuid
}
