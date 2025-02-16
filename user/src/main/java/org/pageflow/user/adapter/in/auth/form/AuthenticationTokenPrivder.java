package org.pageflow.user.adapter.in.auth.form;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.user.adapter.in.filter.shared.SessionPrincipal;
import org.pageflow.user.domain.token.AccessToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationTokenPrivder {

  public Authentication create(AccessToken accessToken, HttpServletRequest request) {
    SessionPrincipal principal = new SessionPrincipal(accessToken.getUid(), accessToken.getRole());
    WebAuthenticationDetails details = new WebAuthenticationDetails(
      request.getRemoteAddr(), accessToken.getSessionId().toString()
    );

    AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
      principal,
      null,
      principal.getRole().toAuthorities()
    );
    authentication.setDetails(details);

    return authentication;
  }
}
