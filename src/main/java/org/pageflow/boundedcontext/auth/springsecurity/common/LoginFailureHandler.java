package org.pageflow.boundedcontext.auth.springsecurity.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.global.api.code.ApiCode3;
import org.pageflow.global.api.code.ApiCode4;
import org.pageflow.global.api.exception.ApiException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ){
        switch(exception.getClass().getSimpleName()){
            case "UsernameNotFoundException":
                throw new ApiException(ApiCode3.DATA_NOT_FOUND);
            case "BadCredentialsException":
                throw new ApiException(ApiCode4.BAD_CREDENTIALS);
            default:
                throw exception;
        }
    }
}
