package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmailAndEmailVerified(String email, boolean verified);
    
    Account findByUsername(String username);
    
    @EntityGraph(attributePaths = {"profile"})
    Account findFetchJoinProfileByUsername(String username);
    
    Account findByRole(String role);
    
    boolean existsByRole(RoleType roleType);
    
    @EntityGraph(attributePaths = {"profile"})
    Account findFetchJoinProfileById(Long id);
    
    
}
