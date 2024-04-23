package org.pageflow.boundedcontext.auth.application.springsecurity.oauth2.authorizationrequest;

import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface AuthorizationRequestRedisRepo extends CrudRepository<RedisOAuth2AuthorizationRequestWrapper, String> {
}
