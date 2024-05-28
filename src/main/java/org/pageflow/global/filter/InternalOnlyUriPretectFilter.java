package org.pageflow.global.filter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.api.code.ApiCode5;
import org.pageflow.global.api.exception.ApiException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : sechan
 */
@Slf4j
public class InternalOnlyUriPretectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean isInternalOnly = uri.startsWith(UriPrefix.PRIVATE);

        if(isInternalOnly && request.getDispatcherType() != DispatcherType.FORWARD){
            log.warn("private url에 대한 외부요청이 발생하였습니다. uri: {}", uri);
            throw new ApiException(ApiCode5.INTERNAL_SERVER_ERROR);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
