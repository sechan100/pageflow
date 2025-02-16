package org.pageflow.user.port.out.entity;


import org.pageflow.common.shared.jpa.repository.BaseJpaRepository;
import org.pageflow.user.domain.entity.Session;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SessionPersistencePort extends BaseJpaRepository<Session, UUID> {
  @EntityGraph(attributePaths = {"account"})
  Session findWithAccountById(UUID id);

  @Modifying
  int deleteSessionById(UUID id);
}
