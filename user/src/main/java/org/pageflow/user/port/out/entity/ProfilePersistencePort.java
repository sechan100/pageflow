package org.pageflow.user.port.out.entity;

import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.pageflow.user.domain.entity.Profile;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface ProfilePersistencePort extends BaseJpaRepository<Profile, UUID> {
  boolean existsByPenname(String penname);
}
