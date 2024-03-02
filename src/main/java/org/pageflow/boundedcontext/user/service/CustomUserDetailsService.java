package org.pageflow.boundedcontext.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.global.entity.DataNotFoundException;
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
        try {
            Account account = accountRepo.findWithProfileByUsername(username);
            return InitialAuthenticationPrincipal.from(account);
        } catch(DataNotFoundException e){
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }
    }
}