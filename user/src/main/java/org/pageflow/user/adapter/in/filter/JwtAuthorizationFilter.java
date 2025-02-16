package org.pageflow.user.adapter.in.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.user.adapter.in.auth.form.AuthenticationTokenPrivder;
import org.pageflow.user.domain.token.AccessToken;
import org.pageflow.user.port.in.TokenUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  public static final String BEARER = "Bearer ";
  private final TokenUseCase useCase;
  private final AuthenticationTokenPrivder authenticationTokenPrivder;

  @Override
  @SneakyThrows({ServletException.class, IOException.class})
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) {
    if(SecurityContextHolder.getContext().getAuthentication() != null){
      filterChain.doFilter(request, response);
      return;
    }

    Optional<String> accessTokenOpt = resolveToken(request);
    String requestUri = request.getRequestURI();

    // 요청에 토큰이 포함된 경우
    if(accessTokenOpt.isPresent()){
      AccessToken token = useCase.parseAccessToken(accessTokenOpt.get());
      Authentication authentication = authenticationTokenPrivder.create(token, request);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("사용자 JWT로 인증됨: UID({}), role({}), session({})", token.getUid(), token.getRole(), token.getSessionId());
    } else {
      log.debug("사용자가 인증되지 않았습니다(accessToken 없음)");
    }
    filterChain.doFilter(request, response);
  }


  private Optional<String> resolveToken(HttpServletRequest request) {
    String token = null;
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)){
      token = bearerToken.split(" ")[1].trim();
    }
    return Optional.ofNullable(token);
  }
}
