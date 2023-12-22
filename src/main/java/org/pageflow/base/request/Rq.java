package org.pageflow.base.request;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.dto.UserDto;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.domain.user.service.UserService;
import org.springframework.context.ApplicationContext;
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

    // 현재 request에 대한 세션 사용자
    @Setter
    private UserDto userDto;
    
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final ApplicationContext context;
    private final UserService userService;
    private final ProfileRepository profileRepository;


    public Rq(ApplicationContext context, UserService userService, ProfileRepository profileRepository) {

        // [[빈 주입
        ServletRequestAttributes sessionAttributes = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = sessionAttributes.getRequest();
        this.response = sessionAttributes.getResponse();
        this.session = request.getSession();
        this.context = context;
        this.userService = userService;
        this.profileRepository = profileRepository;
        // 빈 주입]]

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            
            this.userDto = ((PrincipalContext) authentication.getPrincipal()).getUserDto();

        } else if (authentication instanceof OAuth2AuthenticationToken) {

            UserDto userDto = ((PrincipalContext) authentication.getPrincipal()).getUserDto();
            String nickname = userDto.getPenname();

            // OAuth를 이용해서 신규로 가입하는 사용자인 경우
            if (nickname == null) {
                this.userDto = UserDto.anonymous();

                // OAuth를 통한 로그인인 경우
            } else {
                this.userDto = userDto;
            }

        } else if (authentication instanceof AnonymousAuthenticationToken) {

            this.userDto = UserDto.anonymous();

        } else {

            this.userDto = null;

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
            log.info("fail to redirect: bean of Rq.java redirect() method");
        }
    }

    
}