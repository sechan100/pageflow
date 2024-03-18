package org.pageflow.global.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.shared.ForwordBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author : sechan
 */
@Component
@RequestScope
@Slf4j
public class Forwarder {
    
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    
    public Forwarder() {
        ServletRequestAttributes servletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = servletRequest.getRequest();
        this.response = servletRequest.getResponse();
    }
    
    /**
     * @param forwardUrl forward할 url
     * @return forwardBuilder
     */
    public ForwordBuilder forwardBuilder(String forwardUrl) {
        try {
            return new ForwordBuilder(request, response, forwardUrl);
        } catch (Exception e) {
            log.error("{}로 forward하지 못했습니다: {}", forwardUrl, e.getMessage());
            throw e;
        }
    }
}
