package org.pageflow.boundedcontext.auth.in.adapter.web;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.dto.Principal;
import org.pageflow.boundedcontext.auth.in.port.TokenUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

  @Override
  @SneakyThrows({ServletException.class, IOException.class})
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) {
    if(SecurityContextHolder.getContext().getAuthentication()!=null){
      filterChain.doFilter(request, response);
      return;
    }
    Optional<String> accessTokenOp = resolveTokenIfExist(request);
    String requestURI = request.getRequestURI();

    if(accessTokenOp.isPresent()){
      Principal.Session principal = useCase.extractPrincipalFromAccessToken(accessTokenOp.get());
      Authentication authentication = new UsernamePasswordAuthenticationToken(
        principal,
        null,
        principal.getRole().toAuthorities()
      );

      // Authentication 객체를 할당하면, 이후 AnonymousAuthenticationFilter에서 인증된 사용자로 처리한다.
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("인증된 사용자 요청: URI({}), UID({}), ROLE({})", requestURI, principal.getUid().toLong(), principal.getRole());
    } else {
      log.debug("익명 사용자 요청: {}", requestURI);
    }
    filterChain.doFilter(request, response);
  }


  private Optional<String> resolveTokenIfExist(HttpServletRequest request) {
    String token = null;
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)){
      token = bearerToken.split(" ")[1].trim();
    }
    return Optional.ofNullable(token);
  }
}
