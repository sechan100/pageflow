package org.pageflow.boundedcontext.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.pageflow.boundedcontext.user.dto.principal.OnlyAuthProcessPrincipal;
import org.pageflow.shared.data.query.TryQuery;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * for form login: UserDetailsService interface custom impl
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserQueries queris;

    @Override
    @Transactional(readOnly = true)
    public OnlyAuthProcessPrincipal loadUserByUsername(String username) {
        TryQuery<AccountEntity> findByUsername = TryQuery.of(() -> repo.findWithProfileByUsername(username));
        AccountEntity account = findByUsername.findOrElseThrow(
            () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다")
        );
        return OnlyAuthProcessPrincipal.from(account);
    }
}