package org.pageflow.boundedcontext.user.repository;


import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmailAndEmailVerified(String email, boolean verified);

    boolean existsByRole(RoleType roleType);

    AccountEntity findByUsername(String username);

    AccountEntity findByRole(String role);

    @EntityGraph(attributePaths = {"profile"})
    AccountEntity findWithProfileByUsername(String username);

    @EntityGraph(attributePaths = {"profile"})
    AccountEntity findWithProfileById(Long id);

}
