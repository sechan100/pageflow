package org.pageflow.user.adapter.in.auth.form;


import lombok.RequiredArgsConstructor;
import org.pageflow.user.adapter.in.auth.ForwardRequireAuthenticationPrincipal;
import org.pageflow.user.adapter.in.auth.LoginTokenEndpointForward;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.dto.UserDto;
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
    User user = loadAccountPort.load(username)
      .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. username:" + username));

    LoginTokenEndpointForward forward = LoginTokenEndpointForward.of(new UserDto(user));
    return ForwardRequireAuthenticationPrincipal.form(forward, user.getUsername(), user._getPassword());
  }
}