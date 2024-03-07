package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);
    
    boolean existsByEmailAndEmailVerified(String email, boolean verified);
    
    boolean existsByRole(RoleType roleType);
    
    Account findByUsername(String username);
    
    Account findByRole(String role);
    
    @EntityGraph(attributePaths = {"profile"})
    Account findWithProfileByUsername(String username);
    
    @EntityGraph(attributePaths = {"profile"})
    Account findWithProfileByUID(Long UID);
    
}
