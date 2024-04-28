package org.pageflow.global.api;


import com.google.common.base.Preconditions;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.application.dto.Principal;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.global.api.code.Code1;
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
 *
 */
@Component
@RequestScope
@Slf4j
public class RequestContext {
    
    private final Principal.Base principal;
    private final HttpServletRequest request;
    private final HttpServletResponse response;


    public RequestContext() {
        ServletRequestAttributes servletRequest = Preconditions.checkNotNull(
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()),
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

        // 익명 사용자
        if(authentication instanceof AnonymousAuthenticationToken) {
            this.principal = Principal.Session.anonymous();
        // 인증된 사용자
        } else {
            Object principal = authentication.getPrincipal();
            Preconditions.checkArgument(
                    Principal.Base.class.isAssignableFrom(principal.getClass()),
                    "처리할 수 없는 Principal 객체타입: " + principal.getClass().getName()
            );
            this.principal = (Principal.Base) principal;
            }
        }
    
    
    /**
     * @param attrName request에 저장된 속성의 이름
     * @param <R> type of Request Attribute
     */
    public <R> R getRequestAttr(String attrName) {
        return (R) request.getAttribute(attrName);
    }
    
    /**
     * @param name 쿠키 이름
     */
    public Optional<Cookie> getCookie(String name) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
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
        } catch (IOException e) {
            log.error("{}로 redirect에 실패했습니다: {}", redirectUrl, e.getMessage());
        }
    }

    /**
     * @return 현재 로그인한 사용자의 UID
     * @apiNote 만약 로그인된 사용자가 아닌 경우 Code1.LOGIN_REQUIRED을 발생함.
     */
    public UID getUID() {
        UID uid = principal.getUid();
        if(uid.equals(UID.from(0L))) throw Code1.LOGIN_REQUIRED.fire();
        else return uid;
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