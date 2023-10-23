package org.pageflow.base.request;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.dto.UserSession;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;


@Component
@RequestScope
@Getter
@Slf4j
public class Rq {
    
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final UserSession userSession;
    private final ApplicationContext context;
    private final AccountService accountService;
    
    
    public Rq(HttpServletRequest req, HttpServletResponse resp, HttpSession session, ApplicationContext context, AccountService accountService) {
        this.request = req;
        this.response = resp;
        this.session = session;
        this.context = context;
        this.accountService = accountService;
        
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            
            UserSession userSession = ((PrincipalContext)authentication.getPrincipal()).getUserSession();
            userSession.setLogin(true);
            this.userSession = userSession;
            
        } else if(authentication instanceof OAuth2AuthenticationToken) {
            
            UserSession userSession = ((PrincipalContext)authentication.getPrincipal()).getUserSession();
            String nickname = userSession.getNickname();
            
            // OAuth를 이용해서 신규로 가입하는 사용자인 경우
            if(nickname == null){
                this.userSession = UserSession.anonymousUserSession();
                
            // OAuth를 통한 로그인인 경우
            } else {
                userSession.setLogin(true);
                this.userSession = userSession;
            }
            
        } else if(authentication instanceof AnonymousAuthenticationToken) {
            
            this.userSession = UserSession.anonymousUserSession();
            
        } else {
            
            this.userSession = null;
            
        }
    }
    
    
    public void setRequestAttr(String attrName, Object attrValue) {
        request.setAttribute(attrName, attrValue);
    }
    
    public <R> R getRequestAttr(String attrName) {
        return (R) request.getAttribute(attrName);
    }

    public Cookie getCookie(String name){
        
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
    
    public String alert(String msg, String redirectUrl){
        return "<script>alert('" + msg + "'); location.href='" + redirectUrl + "';</script>";
    }
    
    public String historyBack(String msg){
        return "<script>alert('" + msg + "'); history.back();</script>";
    }
    
    public void redirect(String redirectUrl) {
        try{
            response.sendRedirect(redirectUrl);
        } catch(IOException e){
            log.info("fail to redirect: bean of Rq.java redirect() method");
        }
    }
    
    public Account getAccount() {
        return accountService.findByUsername(userSession.getUsername());
    }
}