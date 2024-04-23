package org.pageflow.boundedcontext.user.adapter.out.persistence.repository;


import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.AccountJpaEntity;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface AccountJpaRepository extends BaseJpaRepository<AccountJpaEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmailAndEmailVerified(String email, boolean emailVerified);

    boolean existsByRole(RoleType roleType);

    Optional<AccountJpaEntity> findByUsername(String username);

    Optional<AccountJpaEntity> findByRole(RoleType role);

    @EntityGraph(attributePaths = {"profile"})
    Optional<AccountJpaEntity> findWithProfileByUsername(String username);

    @EntityGraph(attributePaths = {"profile"})
    Optional<AccountJpaEntity> findWithProfileById(Long id);

}
