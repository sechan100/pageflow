package org.pageflow.global.request;


import com.google.common.base.Preconditions;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.user.model.principal.PageflowPrincipal;
import org.pageflow.boundedcontext.user.model.principal.SessionPrincipal;
import org.pageflow.boundedcontext.user.repository.ProfileRepo;
import org.pageflow.global.api.code.exception.BizException;
import org.pageflow.util.ForwordBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


/**
 *
 */
@Component
@RequestScope
@Slf4j
public class RequestContext {
    
    private final PageflowPrincipal principal;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ProfileRepo profileRepo;
    
    public RequestContext(ProfileRepo profileRepo) {
        
        ServletRequestAttributes servletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = servletRequest.getRequest();
        this.response = servletRequest.getResponse();
        
        // DI
        this.profileRepo = profileRepo;
        
        // 인증객체 참조
        Authentication authentication = Preconditions.checkNotNull(
                SecurityContextHolder.getContext().getAuthentication(),
                "인증객체가 존재하지 않습니다. 'SecurityContextHolder.getContext().getAuthentication()'"
        );
        
        // 인증상태 점검
        Preconditions.checkState(
                /*
                 * AnonymousAuthenticationToken이어도 authenticated 필드는 true임
                 * 쉽게 생각해서,
                 * Token 내부의 authenticated 필드 -> Provider에 의해서 검증되었냐 안되었냐 여부
                 * Token 타입 -> 인증 타입(form, OAuth2, 익명)
                 * */
                authentication.isAuthenticated(),
                "Authentication(인증객체)의 authenticated 필드가 false입니다. " +
                        "AuthenticationProvider가 정상적으로 작동하지 않았거나, 임의로 필드가 변경되었을 수 있습니다."
        );
        
        
        // 사용자 타입 분류
        // 익명 사용자
        if(authentication instanceof AnonymousAuthenticationToken) {
            this.principal = SessionPrincipal.anonymous();
            
        // 인증된 사용자
        } else {
            Object principal = authentication.getPrincipal();
            Preconditions.checkArgument(
                    PageflowPrincipal.class.isAssignableFrom(principal.getClass()),
                    "처리할 수 없는 Principal 객체타입: " + principal.getClass().getName()
            );
            this.principal = (PageflowPrincipal) principal;
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
     * @param forwardUrl forward할 url
     * @return forwardBuilder
     */
    public ForwordBuilder forwardBuilder(String forwardUrl) {
        try {
            return new ForwordBuilder(request, response, forwardUrl);
        } catch (Exception e) {
            log.error("{}로 forward하지 못했습니다: {}", forwardUrl, e.getMessage());
            throw e;
        }
    }
    
    /**
     * @return 현재 로그인한 사용자의 UID
     */
    public Long getUID() {
        return principal.getUID();
    }
    
    /**
     * BizException을 처리하여 반환해주는 컨트롤러로 매핑함
     */
    public void delegateBizExceptionHandling(BizException e){
        request.setAttribute(BizException.class.getSimpleName(), e);
        forwardBuilder("/internal/throw/biz").forward();
    }
}