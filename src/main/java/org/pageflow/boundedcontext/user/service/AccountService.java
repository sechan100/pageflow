package org.pageflow.boundedcontext.user.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    @Getter
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    
  
    
    
    
    
    
    // ****************************************************
    // *********     JPA Repository service      **********
    // ****************************************************
    
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
    
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }
    
    public boolean existsByEmailAndProvider(String email, String provider) {
        return accountRepository.existsByEmailAndProvider(email, provider);
    }
}
