package org.pageflow.user.adapter.in.filter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.api.UriPrefix;
import org.pageflow.user.adapter.in.auth.form.PrivateUriAccessException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * {@link UriPrefix}의 PRIVATE로 시작하는 URI에 대한 요청을 막는 필터.
 *
 * PRIVATE는 오직 forward로만 접근 가능한 URI이다.
 * @author : sechan
 */
@Slf4j
public class PrivateUriPretectFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String uri = request.getRequestURI();
    boolean isPrivate = uri.startsWith(UriPrefix.PRIVATE);

    if(isPrivate && request.getDispatcherType() != DispatcherType.FORWARD){
      log.warn("private url에 대한 외부요청이 발생하였습니다. uri: {}", uri);
      throw new PrivateUriAccessException(uri);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
