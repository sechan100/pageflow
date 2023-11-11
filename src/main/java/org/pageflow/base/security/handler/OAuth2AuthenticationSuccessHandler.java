package org.pageflow.base.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends FormLoginAuthenticationSuccessHandler {

    public OAuth2AuthenticationSuccessHandler(Rq rq) {
        super(rq);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (authentication != null) {
            String userRole = ((PrincipalContext) authentication.getPrincipal()).getUserSession().getRole();
            if (!userRole.equals("ROLE_ANONYMOUS")) {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }

    }
}
