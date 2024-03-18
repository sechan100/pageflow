package org.pageflow.boundedcontext.user.entity;

import lombok.Builder;
import lombok.Getter;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.global.data.Entity;
import org.pageflow.shared.TimeIntorducer;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * 회원가입시 여러단계를 거칠 수 있는데, 이때 사용자가 서버에서 지정해준 값을 임의로 변경하는 것을 막기 위하여
 * 서버 메모리에 임시로 저장되는 사용자 회원가입 form 데이터.
 * @author : sechan
 */
@Getter
@Builder
@RedisHash(value = "user-signup", timeToLive = TimeIntorducer.Seconds.HOUR)
public class SignupCache implements Entity {

    @Id
    private String username;
    
    // (변경 X) 캐싱되지 않은 것은 모두 NATIVE로 간주.
    private ProviderType provider;
    
    private String email;
    
    private String penname;
    
    private String profileImgUrl;
    
}
