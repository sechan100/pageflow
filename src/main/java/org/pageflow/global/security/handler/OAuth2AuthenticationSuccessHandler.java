package org.pageflow.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

//        if (authentication != null) {
//            RoleType userRole = ((PrincipalContext) authentication.getPrincipal()).getUserDto().getRole();
//            if (!userRole.equals(RoleType.ROLE_ANONYMOUS)) {
//                super.onAuthenticationSuccess(request, response, authentication);
//            }
//        }

    }
}
