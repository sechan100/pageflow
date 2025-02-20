package org.pageflow.user.port.out.entity;


import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.pageflow.common.user.RoleType;
import org.pageflow.user.domain.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

public interface AccountPersistencePort extends BaseJpaRepository<Account, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(RoleType roleType);

  Optional<Account> findByUsername(String username);

  Optional<Account> findByRole(RoleType role);

  @EntityGraph(attributePaths = {"profile"})
  Optional<Account> findWithProfileByUsername(String username);

  @EntityGraph(attributePaths = {"profile"})
  Optional<Account> findWithProfileById(UUID id);

}
