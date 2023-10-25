package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * for form login: UserDetailsService interface custom impl
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final AccountRepository accountRepository;
    
    @Override
    public PrincipalContext loadUserByUsername(String username) {
        
        Account account = accountRepository.findByUsernameWithProfile(username);
        
        // username not found check
        if(account == null) {
            throw new UsernameNotFoundException("Account Entity found by username '" + username + "' does not exist.");
        }
        
        return new PrincipalContext(account);
    }
}