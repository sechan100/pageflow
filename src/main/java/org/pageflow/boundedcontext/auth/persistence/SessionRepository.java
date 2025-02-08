package org.pageflow.boundedcontext.auth.persistence;


import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SessionRepository extends BaseJpaRepository<Session, UUID> {
  @EntityGraph(attributePaths = {"account"})
  Session findWithAccountById(UUID id);
}
