package org.pageflow.base.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProps;
import org.pageflow.base.request.Rq;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final CustomProps customProps;
    private final Rq rq;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception
    ) throws IOException, ServletException {

        // UsernameNotFoundException
        if (exception instanceof UsernameNotFoundException) {

            response.sendRedirect(
                    customProps.getSite().getLoginFormUri()
            );

            // BadCredentialsException
        } else if (exception instanceof BadCredentialsException) {

            response.sendRedirect(
                    customProps.getSite().getLoginFormUri()
            );

        }

    }
}