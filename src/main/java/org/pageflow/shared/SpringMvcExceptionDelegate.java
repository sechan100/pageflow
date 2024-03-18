package org.pageflow.shared;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * servlet 필터에서 발생한 예외를 SpringMVC에 통합하여 처리할 수 있도록 위임해주는 클래스
 * @author : sechan
 */
@RequestScope
@Component
public class SpringMvcExceptionDelegate {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String exceptionDelegateEndpoint = "/internal/throw/exception";
    public static final String EXCEPTION_REQUEST_ATTR_NAME = "SpringMvcExceptionDelegate_delegated_exception";

    public SpringMvcExceptionDelegate() {
        ServletRequestAttributes servletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = servletRequest.getRequest();
        this.response = servletRequest.getResponse();
    }

    public void throwing(Exception e) {
        request.setAttribute(EXCEPTION_REQUEST_ATTR_NAME, e);
        new ForwordBuilder(request, response, exceptionDelegateEndpoint).forward();
    }

    public Exception getDelegatedException() {
        return (Exception) request.getAttribute(EXCEPTION_REQUEST_ATTR_NAME);
    }
}
