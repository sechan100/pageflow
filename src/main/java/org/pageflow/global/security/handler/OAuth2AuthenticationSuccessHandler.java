package org.pageflow.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.global.request.RequestContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends FormLoginAuthenticationSuccessHandler {

    public OAuth2AuthenticationSuccessHandler(RequestContext requestContext) {
        super(requestContext);
    }

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
