package org.pageflow.boundedcontext.user.cache;

import lombok.*;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.shared.data.entity.Entity;
import org.pageflow.shared.utils.TimeIntorducer;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * <p>OAuth2 회원가입시 2번의 요청에 걸쳐서 회원가입된다.</p>
 * <p>이때 사용자가 서버에서 지정해준 값을 임의로 변경하는 것을 막기 위하여
 * 서버 메모리에 임시로 저장되는 사용자 회원가입 form 데이터.</p>
 * @author : sechan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "user-signup", timeToLive = TimeIntorducer.Seconds.HOUR)
public class OAuth2PresignupCache implements Entity {

    @Id
    private String username;

    @Setter(AccessLevel.NONE)
    private ProviderType provider;

    private String email;
    
    private String penname;
    
    private String profileImgUrl;
    
}
