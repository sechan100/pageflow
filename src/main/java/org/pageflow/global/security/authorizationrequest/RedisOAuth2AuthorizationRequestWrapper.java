package org.pageflow.global.security.authorizationrequest;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 * {@link OAuth2AuthorizationRequest}가 상속이 안되서 wrapper로 만듦
 * @author : sechan
 */
@RequiredArgsConstructor
@RedisHash("oauth2_authorization_request")
public class RedisOAuth2AuthorizationRequestWrapper {
    
    @Id
    private final String state;
    
    @Getter
    private final OAuth2AuthorizationRequest request;
    
    
}
