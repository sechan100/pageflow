package org.pageflow.boundedcontext.user.adapter.out.cache.entity;

import lombok.*;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.shared.jpa.JpaEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * <p>OAuth2 회원가입시 2번의 요청에 걸쳐서 회원가입된다.</p>
 * <p>이때 사용자가 서버에서 지정해준 값을 임의로 변경하는 것을 막기 위하여
 * 서버 메모리에 임시로 저장되는 사용자 회원가입 form 데이터.</p>
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "oauth2-pre-signup", timeToLive = 60 * 60)
public class OAuth2PreSignupCache implements JpaEntity {

    @Id
    private String username;

    @Setter(AccessLevel.NONE)
    private ProviderType provider;

    private String profileImageUrl;
    
}
