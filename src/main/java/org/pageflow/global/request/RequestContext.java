package org.pageflow.global.request;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.domain.user.service.DefaultUserService;
import org.pageflow.infra.util.ForwordBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class RequestContext {
    
    private final PrincipalContext principal;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final DefaultUserService defaultUserService;
    private final ProfileRepository profileRepository;
    
    public RequestContext(DefaultUserService userService, ProfileRepository profileRepository) {

        // [[빈 주입
        ServletRequestAttributes sessionAttributes = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = sessionAttributes.getRequest();
        this.response = sessionAttributes.getResponse();
        this.session = request.getSession();
        this.defaultUserService = userService;
        this.profileRepository = profileRepository;
        // 빈 주입]]

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // UsernamePasswordAuthenticationToken, OAuth2AuthenticationToken인 경우
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            this.principal = (PrincipalContext) authentication.getPrincipal();
            
        // AnonymousAuthenticationToken인 경우
        } else {
            this.principal = PrincipalContext.anonymous();
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
            log.error("{}로 redirect에 실패했습니다: {}", redirectUrl, e.getMessage());
        }
    }
    
    public ForwordBuilder forwardBuilder(String forwardUrl) {
        try {
            return new ForwordBuilder(request, response, forwardUrl);
        } catch (Exception e) {
            log.error("{}로 forward하지 못했습니다: {}", forwardUrl, e.getMessage());
            throw e;
        }
    }
    
    public Long getUID() {
        return principal.getId();
    }
    
    public String getUsername() {
        return principal.getUsername();
    }
    
    
}