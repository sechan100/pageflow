package org.pageflow.base.request;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.domain.user.service.DefaultUserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Objects;


@Component
@RequestScope
@Getter
@Slf4j
public class Rq {
    
    private PrincipalContext principal;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final DefaultUserService defaultUserService;
    private final ProfileRepository profileRepository;
    
    public Rq(DefaultUserService userService, ProfileRepository profileRepository) {

        // [[빈 주입
        ServletRequestAttributes sessionAttributes = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = sessionAttributes.getRequest();
        this.response = sessionAttributes.getResponse();
        this.session = request.getSession();
        this.defaultUserService = userService;
        this.profileRepository = profileRepository;
        // 빈 주입]]

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            
            this.principal = (PrincipalContext) authentication.getPrincipal();

        } else if (authentication instanceof OAuth2AuthenticationToken) {

            this.principal = (PrincipalContext) authentication.getPrincipal();

        } else if (authentication instanceof AnonymousAuthenticationToken) {

            this.principal = PrincipalContext.anonymous();

        } else {

            this.principal = null;

        }
    }

    
    
    public <R> R getRequestAttr(String attrName) {
        return (R) request.getAttribute(attrName);
    }

    public Cookie getCookie(String name) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }
    
    public void redirect(String redirectUrl) {
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.info("Redirect 실패: {}", e.getMessage());
        }
    }
    
    public Long getId() {
        return principal.getId();
    }
    
    public String getUsername() {
        return principal.getUsername();
    }
    
    
}