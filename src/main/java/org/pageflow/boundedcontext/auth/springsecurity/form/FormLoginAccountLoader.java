package org.pageflow.boundedcontext.auth.springsecurity.form;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.application.acl.LoadAccountAcl;
import org.pageflow.boundedcontext.auth.application.dto.Principal;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * for form login: UserDetailsService interface custom impl
 */
@Service
@RequiredArgsConstructor
public class FormLoginAccountLoader implements UserDetailsService {

    private final LoadAccountAcl accountAcl;

    @Override
    @Transactional(readOnly = true)
    public Principal.OnlyInAuthing loadUserByUsername(String username) {
        Account account = accountAcl.load(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username)
        );

        return new Principal.OnlyInAuthing(account);
    }
}