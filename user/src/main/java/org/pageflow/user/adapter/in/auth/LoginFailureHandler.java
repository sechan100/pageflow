package org.pageflow.user.adapter.in.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.common.result.ResultException;
import org.pageflow.user.application.UserCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception
  ) {
    switch(exception.getClass().getSimpleName()) {
      case "UsernameNotFoundException", "BadCredentialsException":
        throw new ResultException(UserCode.BAD_CREDENTIALS);
      default:
        throw exception;
    }
  }
}
