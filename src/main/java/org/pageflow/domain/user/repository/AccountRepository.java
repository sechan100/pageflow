package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmailAndEmailVerified(String email, boolean verified);
    boolean existsByRole(RoleType roleType);
    
    Account findByUsername(String username);
    Account findByRole(String role);
    
    @EntityGraph(attributePaths = {"profile"})
    Account findFetchJoinProfileByUsername(String username);
    @EntityGraph(attributePaths = {"profile"})
    Account findFetchJoinProfileById(Long id);
    
    
}
