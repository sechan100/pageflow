package org.pageflow.user.adapter.in.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.utility.Forward;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * SpringSecurity에서 인증된 요청들의 추가처리를 위해서 forward 시켜주는 handler
 * {@link ForwardRequireAuthenticationPrincipal}을 참고
 */
@Component
@RequiredArgsConstructor
public class SuccessAuthenticatonForwardHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    assert authentication != null;
    if(authentication.getPrincipal() instanceof ForwardRequireAuthenticationPrincipal principal){
      Forward forward = principal.getForward();
      forward.send();
    } else {
      throw new IllegalStateException(
        "SpringSecurity를 통해서 인증된 사용자의 Principal이 ForwardRequireAuthenticationPrincipal이 아닙니다."
      );
    }
  }
}
