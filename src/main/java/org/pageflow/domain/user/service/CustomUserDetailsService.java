package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.domain.user.repository.AccountRepository;
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

    private final AccountRepository accountRepository;

    @Override
    public InitialAuthenticationPrincipal loadUserByUsername(String username) {
        try {
            Account account = accountRepository.findWithProfileByUsername(username);
            return InitialAuthenticationPrincipal.from(account);
        } catch(DataNotFoundException e){
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }
    }
}