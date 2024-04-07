package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.cache.OAuth2PresignupCache;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface OAuth2PresignupCacheRepository extends CrudRepository<OAuth2PresignupCache, String> {
}
