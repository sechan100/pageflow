package org.pageflow.boundedcontext.auth.springsecurity.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.global.result.code.ResultCode1;
import org.pageflow.global.result.exception.ApiException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException authException
  ) {
    if(authException instanceof InsufficientAuthenticationException insufficientAuthenticationException){
      throw new ApiException(ResultCode1.LOGIN_REQUIRED);
    }
  }
}