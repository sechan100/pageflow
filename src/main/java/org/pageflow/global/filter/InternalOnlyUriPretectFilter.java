package org.pageflow.global.filter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.global.api.code.Code2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : sechan
 */
public class InternalOnlyUriPretectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean isInternalOnly = uri.startsWith(UriPrefix.PRIVATE);

        if(isInternalOnly && request.getDispatcherType() != DispatcherType.FORWARD){
            throw Code2.PROTECTED_URI_ACCESS.fire();
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
