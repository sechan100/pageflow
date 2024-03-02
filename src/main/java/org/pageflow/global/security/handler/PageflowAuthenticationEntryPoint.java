package org.pageflow.global.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.pageflow.global.request.RequestContext;
import org.pageflow.global.api.code.exception.BizException;
import org.pageflow.global.api.code.SessionCode;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PageflowAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final RequestContext requestContext;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) {
        
        if (authException instanceof InsufficientAuthenticationException insufficientAuthenticationException) {
            /* SecurityFilterChain은, MVC 디스패처보다 먼저 실행되기 때문에 @RestControllerAdvice로 위임되지 않는다.
             * 때문에 이의 처리를 위해 Exception을 처리하여 공통응답을 반환하는 Controller로 직접 forward하는 것.
             * */
            requestContext.delegateBizExceptionHandling(new BizException(SessionCode.LOGIN_REQUIRED));
        }
        
    }
}