package org.pageflow.boundedcontext.auth.springsecurity.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.global.result.code.ResultCode3;
import org.pageflow.global.result.code.ResultCode4;
import org.pageflow.global.result.exception.ApiException;
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
    switch(exception.getClass().getSimpleName()){
      case "UsernameNotFoundException":
        throw new ApiException(ResultCode3.DATA_NOT_FOUND);
      case "BadCredentialsException":
        throw new ApiException(ResultCode4.BAD_CREDENTIALS);
      default:
        throw exception;
    }
  }
}
