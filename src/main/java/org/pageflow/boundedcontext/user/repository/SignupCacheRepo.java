package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.entity.SignupCache;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface SignupCacheRepo extends CrudRepository<SignupCache, String> {
}
