package org.pageflow.boundedcontext.auth.adapter.out.persistence.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.pageflow.shared.jpa.BaseJpaEntity;
import org.pageflow.shared.type.TSID;
import org.springframework.data.redis.core.RedisHash;


/**
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("email-verification-request")
public class EVRedisEntity extends BaseJpaEntity {
    @Id
    private TSID uid;
    private String email;
    private String authCode; // uuid
}
