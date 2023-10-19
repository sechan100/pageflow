package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
    boolean existsByUsername(String username);
    
    boolean existsByEmailAndProvider(String email, String provider);
    
    Account findByUsername(String username);
    
}
