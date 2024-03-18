package org.pageflow.boundedcontext.user.security;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.shared.query.TryQuery;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * for form login: UserDetailsService interface custom impl
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepo accountRepo;

    @Override
    public InitialAuthenticationPrincipal loadUserByUsername(String username) {
        
        TryQuery<Account> findByUsername = TryQuery.of(() -> accountRepo.findWithProfileByUsername(username));
        
        return InitialAuthenticationPrincipal.from(
            findByUsername.findOrElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"))
        );
    }
}