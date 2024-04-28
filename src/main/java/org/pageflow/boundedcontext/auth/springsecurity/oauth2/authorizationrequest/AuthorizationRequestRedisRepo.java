package org.pageflow.boundedcontext.auth.springsecurity.oauth2.authorizationrequest;

import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface AuthorizationRequestRedisRepo extends CrudRepository<RedisOAuth2AuthorizationRequestWrapper, String> {
}
