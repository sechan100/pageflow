package org.pageflow.base.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.request.Rq;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public CustomAuthenticationEntryPoint(CustomProperties customProperties, Rq rq) {
        super(customProperties.getSite().getLoginFormUri());
        this.customProperties = customProperties;
        this.rq = rq;
    }

    private final CustomProperties customProperties;
    private final Rq rq;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        String redirectUri = request.getRequestURI();
        String queryString;

        // anonymous가 authenticated에 접근
        if (authException instanceof InsufficientAuthenticationException insufficientAuthenticationException) {

            new LoginUrlAuthenticationEntryPoint(
                    customProperties.getSite().getLoginFormUri()
            ).commence(request, response, insufficientAuthenticationException);

        }
    }
}