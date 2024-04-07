package org.pageflow.global.security.authorizationrequest;


import lombok.Builder;
import lombok.Getter;
import org.pageflow.shared.data.entity.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 * {@link OAuth2AuthorizationRequest}가 상속이 안되서 wrapper로 만듦
 * @author : sechan
 */
@Builder
@RedisHash("oauth2_authorization_request")
public class RedisOAuth2AuthorizationRequestWrapper implements Entity {
    
    @Id
    private String state;
    
    @Getter
    private OAuth2AuthorizationRequest request;
    
    
}
