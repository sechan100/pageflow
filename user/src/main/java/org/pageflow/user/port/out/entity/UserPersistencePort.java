package org.pageflow.user.port.out.entity;


import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.pageflow.common.user.RoleType;
import org.pageflow.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserPersistencePort extends BaseJpaRepository<User, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(RoleType roleType);

  Optional<User> findByUsername(String username);

  Optional<User> findByRole(RoleType role);

  boolean existsByEmailAndIsEmailVerifiedIsTrue(String email);
}
