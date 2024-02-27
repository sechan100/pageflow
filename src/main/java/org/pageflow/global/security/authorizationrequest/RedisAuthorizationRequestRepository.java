package org.pageflow.global.security.authorizationrequest;

import org.springframework.data.repository.CrudRepository;

/**
 * redis repositiry
 * @author : sechan
 */
public interface RedisAuthorizationRequestRepository extends CrudRepository<RedisOAuth2AuthorizationRequestWrapper, String> {
}
