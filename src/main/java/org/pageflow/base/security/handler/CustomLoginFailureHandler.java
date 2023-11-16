package org.pageflow.base.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.request.AlertType;
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

    private final CustomProperties customProperties;
    private final Rq rq;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // UsernameNotFoundException
        if (exception instanceof UsernameNotFoundException) {

            response.sendRedirect(
                    rq.getAlertStorageRedirectUri(AlertType.ERROR, "존재하지 않는 아이디입니다.", null)
            );

            // BadCredentialsException
        } else if (exception instanceof BadCredentialsException) {

            response.sendRedirect(
                    rq.getAlertStorageRedirectUri(AlertType.ERROR, "비밀번호가 일치하지 않습니다.", null)
            );

        }

    }
}