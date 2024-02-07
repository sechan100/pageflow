package org.pageflow.global.request;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.domain.user.model.principal.PageflowPrincipal;
import org.pageflow.domain.user.model.principal.SessionPrincipal;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.global.exception.business.exception.BizException;
import org.pageflow.util.ForwordBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
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
    private final ProfileRepository profileRepository;
    
    public RequestContext(ProfileRepository profileRepository) throws IllegalClassFormatException {
        
        ServletRequestAttributes sessionAttributes = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = sessionAttributes.getRequest();
        this.response = sessionAttributes.getResponse();
        
        // DI
        this.profileRepository = profileRepository;
        
        /*
        * 로그인 실패, 로그인 안한 상태로 authenticated에 접근 -> authentication == null
        * 필터를 모두 거친 상태에서... UsernamePasswordAuthenticationToken, OAuth2AuthenticationToken, AnonymousAuthenticationToken 중 하나
        * */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = true;
        
        if(authentication != null) {
            // UsernamePasswordAuthenticationToken, OAuth2AuthenticationToken인 경우
            if(authentication.isAuthenticated()) { // AnonymousAuthenticationToken이어도 이건 true임
                isAnonymous = false;
            }
        }
        
        // 익명 사용자
        if(isAnonymous) {
            this.principal = InitialAuthenticationPrincipal.anonymous();
            
        // 로그인한 사용자
        } else {
            // 최초 로그인을 하여 세션을 생성중인 경우
            if(authentication.getPrincipal() instanceof InitialAuthenticationPrincipal){
                this.principal = (PageflowPrincipal) authentication.getPrincipal();
                
            // 이미 로그인한 세션에 AccessToken으로 인증하는 경우
            } else if(authentication.getPrincipal() instanceof SessionPrincipal){
                this.principal = (PageflowPrincipal) authentication.getPrincipal();
            } else {
                throw new IllegalArgumentException("처리할 수 없는 Principal 객체타입: " + authentication.getPrincipal().getClass().getName());
            }
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