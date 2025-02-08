package org.pageflow.boundedcontext.auth.springsecurity.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.dto.Principal;
import org.pageflow.global.filter.InFilterForwardManager;
import org.pageflow.shared.utility.Forward;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
  private final InFilterForwardManager inFilterForwardManager;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    assert authentication!=null;
    assert authentication.getPrincipal() instanceof Principal.OnlyInAuthing;
    Principal.OnlyInAuthing principal = ((Principal.OnlyInAuthing) authentication.getPrincipal());

    // 요청 분기후, 더미 객체를 반환해서 SuccessHandler가 호출될 수 있음
    boolean isReallySuccess = !principal.isInFilterForwarded();
    if(isReallySuccess){
      // AccessToken과 RefreshToken을 반환하기위한 Controller로 forward
      Forward loginForward = InAuthingInFilterForwardFactory.getLoginFoward(principal.getAuthedAccount());
      inFilterForwardManager.inFilterForward(this, loginForward);
    }
  }
}
