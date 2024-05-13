package org.pageflow.boundedcontext.auth.springsecurity.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.global.api.code.ApiCode3;
import org.pageflow.global.api.code.ApiCode4;
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
                throw ApiCode3.DATA_NOT_FOUND.feedback("존재하지 않는 아이디입니다.");
            case "BadCredentialsException":
                throw ApiCode4.INVALID_VALUE.feedback(t->t.getPassword_notMatch());
            default:
                throw exception;
        }
    }
}
