package org.pageflow.boundedcontext.auth.application.springsecurity.oauth2.authorizationrequest;


import lombok.*;
import org.pageflow.shared.jpa.JpaEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 * {@link OAuth2AuthorizationRequest}가 상속이 안되서 wrapper로 만듦
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("oauth2-authorization-request")
public class RedisOAuth2AuthorizationRequestWrapper implements JpaEntity {
    @Id
    private String state;
    @Getter
    private OAuth2AuthorizationRequest request;
}
