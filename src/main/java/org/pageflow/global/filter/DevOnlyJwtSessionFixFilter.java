package org.pageflow.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.dto.Principal;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : sechan
 */
@Slf4j
public class DevOnlyJwtSessionFixFilter extends OncePerRequestFilter {
  private Principal.Session fixedPrincipal;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(this.fixedPrincipal==null){
      filterChain.doFilter(request, response);
      return;
    }
    Authentication authentication = new UsernamePasswordAuthenticationToken(
      fixedPrincipal,
      null,
      fixedPrincipal.getRole().toAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.warn("고정세션을 사용하여 인증되었습니다. UID({})", fixedPrincipal.getUid());
    filterChain.doFilter(request, response);
  }


  public void fixSession(UID uid, RoleType role) {
    this.fixedPrincipal = new Principal.Session(
      uid,
      role
    );
  }

  public void clearFixedPrincipal() {
    this.fixedPrincipal = null;
  }
}
