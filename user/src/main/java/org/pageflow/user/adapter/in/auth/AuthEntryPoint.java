package org.pageflow.user.adapter.in.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.code.CommonCode;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인증이 필요한 엔드포인트에 비로그인 사용자가 접근하면 발생하는 예외를 처리하는 클래스
 */
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
      throw new ProcessResultException(CommonCode.LOGIN_REQUIRED);
    }
  }
}