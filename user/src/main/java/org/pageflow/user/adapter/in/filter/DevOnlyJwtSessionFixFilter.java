package org.pageflow.user.adapter.in.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.user.adapter.in.auth.form.AuthenticationTokenPrivder;
import org.pageflow.user.adapter.in.web.JwtSessionFixer;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.domain.token.AccessToken;
import org.pageflow.user.port.in.IssueSessionCmd;
import org.pageflow.user.port.in.SessionUseCase;
import org.pageflow.user.port.in.TokenUseCase;
import org.pageflow.user.port.out.LoadAccountPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author : sechan
 */
@Slf4j
@RequiredArgsConstructor
public class DevOnlyJwtSessionFixFilter extends OncePerRequestFilter implements JwtSessionFixer {
  private final AuthenticationTokenPrivder authenticationTokenPrivder;
  private final SessionUseCase sessionUseCase;
  private final TokenUseCase tokenUseCase;

  // HACK: Read-Only Port라서 일단 'adapter->port'로 참조함
  private final LoadAccountPort loadAccountPort;

  private AccessToken fixedAccessToken;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(this.fixedAccessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }
    Authentication authentication = authenticationTokenPrivder.create(fixedAccessToken, request);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.warn("사용자 개발모드 고정 세션으로 인증됨: UID({}), role({}), session({})", fixedAccessToken.getUid(), fixedAccessToken.getRole(), fixedAccessToken.getSessionId());
    filterChain.doFilter(request, response);
  }


  @Override
  public void fixSession(String username) {
    User user = loadAccountPort.load(username).orElseThrow();
    IssueSessionCmd cmd = new IssueSessionCmd(user.getUid());
    var tokens = sessionUseCase.issueSession(cmd);
    String compact = tokens.getAccessToken().getCompact();
    AccessToken accessToken = tokenUseCase.parseAccessToken(compact).get();
    this.fixedAccessToken = accessToken;
  }

  @Override
  public void clearFixedPrincipal() {
    sessionUseCase.logout(fixedAccessToken.getSessionId());
    this.fixedAccessToken = null;
  }
}
