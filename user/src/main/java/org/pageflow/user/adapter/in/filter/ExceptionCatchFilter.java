package org.pageflow.user.adapter.in.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;


/**
 * <p>SpringSecurity 필터 진행중 발생한 예외를 잡아서 처리하기 위한 Filter</p>
 * <p>
 *   SpringSecurity의 ExceptionTranslationFilter는 AccessDeniedException과 AuthenticationException만을 처리한다.
 *   따라서 다른 모든 Exception는 결국 Servlet Container까지 전파된다.
 *   저 두 예외를 제외한 예외가 발생한 경우, 애플리케이션 공통응답 포멧에 맞게 응답을 처리하기 위한 필터가 바로 이것이다.
 * </p>
 * @author : sechan
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionCatchFilter extends OncePerRequestFilter {
  private final List<HandlerExceptionResolver> resolvers;

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) {
    try {
      filterChain.doFilter(request, response);
    } catch(Exception exception) {
      /**
       * Controller 수준에서 @ExceptionHandler와 같은 처리로 적절하게 예외가 처리되지 않은 경우에는 통과했던
       * 필터로 다시 예외를 보내는데, 때문에 Filter 안에서 발생한 예외가 아니더라도 여기서 잡히는 경우가 생긴다.
       */
      log.debug("SecurityFilterChain 처리중 예외가 발생했거나, Controller에서 처리되지 못한 예외가 전파됐을 수 있습니다: {}", exception.toString());
      resolvers.forEach(resolver -> resolver.resolveException(request, response, null, exception));
    }
  }
}