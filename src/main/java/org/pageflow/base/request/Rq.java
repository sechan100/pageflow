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
import org.springframework.lang.Nullable;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


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
    
    
    public Rq(ApplicationContext context, AccountService accountService) {
        
        ServletRequestAttributes sessionAttributes = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()));
        this.request = sessionAttributes.getRequest();
        this.response = sessionAttributes.getResponse();
        this.session = request.getSession();
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
    
    /**
     * alert 메시지를 띄우고 redirectUri로 이동
     * @param msg : alert에 띄울 메시지
     * @return alertStorage.js로 이동
     */
    public String alert(AlertType alertType, String msg, String redirectUri){
        request.setAttribute("msg", msg);
        request.setAttribute("alertType", alertType.toString().toLowerCase());
        if(redirectUri != null){
            request.setAttribute("redirectUri", redirectUri);
        }
        if(alertType == AlertType.ERROR || alertType == AlertType.WARNING){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        return "/common/alertStorage";
    }
    
    /**
     * alert 메시지를 띄우고 history.back()
     * @param msg : alert에 띄울 메시지
     * @return alertStorage.js로 이동
     */
    public String alert(AlertType alertType, String msg){
        return alert(alertType, msg, null);
    }
    
    /**
     * @param alertType : alertType
     * @param msg : alert에 띄울 메시지
     * @param redirectUri nullable: redirectUri가 null이면 history.back()
     */
    public String getAlertStorageRedirectUri(AlertType alertType, String msg, @Nullable String redirectUri) {
        msg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        if(redirectUri != null){
            return "/common/alertStorage?msg=" + msg + "&alertType=" + alertType.toString().toLowerCase() + "&redirectUri=" + redirectUri;
        } else {
            return "/common/alertStorage?msg=" + msg + "&alertType=" + alertType.toString().toLowerCase();
        }
    }
    
    /**
     * 일반적인 페이지 렌더링 후 응답하는 컨트롤러에서, alert 메세지를 띄울 수 있음.
     * @param alertType : alertType
     * @param msg : alert에 띄울 메시지
     */
    public void setAlert(AlertType alertType, String msg){
        request.setAttribute("oncePerRequestAlert", alertType + ":" + msg);
        if(alertType == AlertType.ERROR || alertType == AlertType.WARNING){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    public void redirect(String redirectUrl) {
        try{
            response.sendRedirect(redirectUrl);
        } catch(IOException e){
            log.info("fail to redirect: bean of Rq.java redirect() method");
        }
    }
    
    public Account getAccount() {
        return accountService.findFetchJoinProfileByUsername(userSession.getUsername());
    }
}