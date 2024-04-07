package org.pageflow.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.pageflow.shared.utils.SpringMvcExceptionDelegate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class ExceptionDelegatingFilter extends OncePerRequestFilter {

    private final SpringMvcExceptionDelegate exceptionDelegate;

    @Override
    @SneakyThrows({IOException.class, ServletException.class})
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ){
        try {
            filterChain.doFilter(request, response);
        } catch(RuntimeException e) {
            exceptionDelegate.throwing(e);
        }
    }
}