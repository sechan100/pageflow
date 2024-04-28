package org.pageflow.boundedcontext.auth.adapter.out.persistence.repository;

import org.pageflow.boundedcontext.auth.adapter.out.persistence.entity.EVRedisEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author : sechan
 */
public interface EVRedisRepository extends CrudRepository<EVRedisEntity, String> {
}
