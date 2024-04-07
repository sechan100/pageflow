package org.pageflow.boundedcontext.user.cache;

import org.pageflow.shared.data.tsid.Tsid;
import org.springframework.data.annotation.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.shared.data.entity.Entity;
import org.pageflow.shared.type.TSID;
import org.pageflow.shared.utils.TimeIntorducer;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;


@Getter
@Setter(AccessLevel.NONE)
@RedisHash(value = "user-email-verification", timeToLive = TimeIntorducer.Seconds.HOUR)
public class EmailVerificationRequest implements Entity {
    
    @Id
    @Tsid
    private Long uid;
    
    private String email;

    /**
     * UUID
     */
    private String authorizationCode;

    public EmailVerificationRequest(TSID uid, String email){
        this.uid = uid.toLong();
        this.email = email;
        this.authorizationCode = UUID.randomUUID().toString();
    }

    public void changeVerificationTargerEmail(String email){
        this.email = email;
    }

    
}
