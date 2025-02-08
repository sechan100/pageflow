package org.pageflow.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.api.DirectApiResponseController;
import org.pageflow.global.result.exception.ApiException;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : sechan
 */
/*
 * @Component 붙이면 그냥 Servlet Filter로도 등록되어버린다.
 * 그럼 SecurityFilterChain에서 1번, 그냥 FilterChain에서 1번해서 총 2번 호출된다
 * */
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionCatchAndDelegatingFilter extends OncePerRequestFilter {
  public static final String API_RESPONSE_REQUEST_ATTR = "ApiExceptionDelegatingFilter.gr";

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) {
    try {
      filterChain.doFilter(request, response);
    } catch(ApiException apiException){
      try {
        log.warn("Filter 안에서 발생한 ApiException을 SpringMVC로 위임하여, 즉시 api 응답: {}", apiException.getMessage());
        request.setAttribute(API_RESPONSE_REQUEST_ATTR, apiException.getApiResponse());
        request
          .getRequestDispatcher(DirectApiResponseController.SEND_GR_ANY_WHERE_ENDPOINT)
          .forward(request, response);
      } catch(ServletException | IOException ex){
        throw new RuntimeException(ex);
      }
    } catch(ServletException | IOException ex){
      throw new RuntimeException(ex);
    }
  }
}