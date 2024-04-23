package org.pageflow.boundedcontext.user.adapter.out.cache.repository;

import org.pageflow.boundedcontext.user.adapter.out.cache.entity.OAuth2PreSignupCache;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface OAuth2PresignupRedisRepo extends CrudRepository<OAuth2PreSignupCache, String> {
}
