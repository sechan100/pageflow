package org.pageflow.common.api;


import com.google.common.base.Preconditions;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.user.UID;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Optional;


/**
 * 반드시 SpringSecurity의 FilterChain을 통과한 후에 사용해야한다.
 * @author sechan
 */
@Component
@RequestScope
@Slf4j
public class RequestContext {

  private final RequestContextUserPrincipal principal;
  private final HttpServletRequest request;
  private final HttpServletResponse response;


  public RequestContext() {
    ServletRequestAttributes servletRequest = Preconditions.checkNotNull(
      ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()),
      "DispatcherServlet이 아직 요청을 처리하지 않은 상태입니다. 'RequestContextHolder.getRequestAttributes()이 null'"
    );
    this.request = servletRequest.getRequest();
    this.response = servletRequest.getResponse();

    // 인증객체 참조
    Authentication authentication = Preconditions.checkNotNull(
      SecurityContextHolder.getContext().getAuthentication(),
      "인증객체가 존재하지 않습니다. 'SecurityContextHolder.getContext().getAuthentication()'"
    );

    // 인증상태 점검
    Preconditions.checkState(
      authentication.isAuthenticated(), """
        Authentication.authenticated == 'false'
        정상적으로 인증제공자를 통과한 인증객체는, 유형에 관계없이 isAuthenticated() == true입니다.
        tip: AuthenticationProvider가 정상적으로 작동하지 않았거나, 임의로 필드가 변경되었을 수 있습니다."""
    );

    // case 1) 익명 사용자인 경우의 principal 객체 생성
    if(authentication instanceof AnonymousAuthenticationToken){
      this.principal = new AnonymouseRequestContextUserPrincipal();
    // case 2) 인증된 사용자의 principal 객체 생성
    } else {
      Object principal = authentication.getPrincipal();

      // case 2-1) 인증된 사용자의 principal이 RequestContextUserPrincipal 타입인 경우
      if(principal instanceof RequestContextUserPrincipal rcuPrincipal){
        if(rcuPrincipal.getUid().equals(UID.ANONYMOUS_UID)){
          throw new IllegalStateException("인증된 사용자의 principal이 익명 사용자의 아이디를 가지고있습니다.");
        }
        this.principal = rcuPrincipal;

      // case 2-2) 인증된 사용자의 principal이 RequestContextUserPrincipal 타입이 아닌 경우(ForwardRequireAuthenticationPrincipal등)
      } else {
        this.principal = new AnonymouseRequestContextUserPrincipal();
        log.debug("인증된 Authentication 객체에 익명 사용자 principal을 할당했습니다. '{}'은 인증된 사용자의 principal이 아닙니다.", principal.getClass().getName());
      }
    }
  }


  /**
   * @param attrName request에 저장된 속성의 이름
   * @param <R>      type of Request Attribute
   */
  public <R> R getRequestAttr(String attrName) {
    return (R) request.getAttribute(attrName);
  }

  /**
   * @param name 쿠키 이름
   */
  public Optional<Cookie> getCookie(String name) {

    Cookie[] cookies = request.getCookies();
    if(cookies!=null){
      for(Cookie cookie : cookies){
        if(cookie.getName().equals(name)){
          return Optional.of(cookie);
        }
      }
    }
    return Optional.empty();
  }

  public void setCookie(Cookie cookie) {
    response.addCookie(cookie);
  }

  public void removeCookie(String name) {
    Cookie cookie = new Cookie(name, "");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  /**
   * @param redirectUrl redirect할 url
   */
  public void redirect(String redirectUrl) {
    try {
      response.sendRedirect(redirectUrl);
    } catch(IOException e){
      log.error("{}로 redirect에 실패했습니다: {}", redirectUrl, e.getMessage());
    }
  }

  /**
   * @return 현재 로그인한 사용자의 UID. 익명 사용자의 경우 익명 UID를 반환한다.
   */
  public UID getUid() {
    UID uid = principal.getUid();
    return uid;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public DispatcherType getDispatcherType() {
    return request.getDispatcherType();
  }

  public boolean isCommitted() {
    return response.isCommitted();
  }
}