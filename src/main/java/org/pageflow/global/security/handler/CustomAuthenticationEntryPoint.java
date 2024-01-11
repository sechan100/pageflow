package org.pageflow.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.request.RequestContext;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public CustomAuthenticationEntryPoint(CustomProps customProps, RequestContext requestContext) {
        super(customProps.getSite().getLoginFormUri());
        this.customProps = customProps;
        this.requestContext = requestContext;
    }

    private final CustomProps customProps;
    private final RequestContext requestContext;

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
                    customProps.getSite().getLoginFormUri()
            ).commence(request, response, insufficientAuthenticationException);

        }
    }
}