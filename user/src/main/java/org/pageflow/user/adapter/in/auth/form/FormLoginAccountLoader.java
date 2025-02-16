package org.pageflow.user.adapter.in.auth.form;


import lombok.RequiredArgsConstructor;
import org.pageflow.user.adapter.in.auth.ForwardRequireAuthenticationPrincipal;
import org.pageflow.user.adapter.in.auth.LoginTokenEndpointForward;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.dto.AccountDto;
import org.pageflow.user.port.out.LoadAccountPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FormLoginAccountLoader implements UserDetailsService {
  private final LoadAccountPort loadAccountPort;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) {
    Account account = loadAccountPort.load(username)
      .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. username:" + username));

    LoginTokenEndpointForward forward = LoginTokenEndpointForward.of(AccountDto.from(account));
    return ForwardRequireAuthenticationPrincipal.form(forward, account.getUsername(), account.getPassword());
  }
}