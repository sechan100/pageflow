package org.pageflow.base.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.AlertType;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.dto.UserSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Rq rq;
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserSession userSession = ((PrincipalContext)authentication.getPrincipal()).getUserSession();
        String nickname = userSession.getNickname();
        
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        
        if(savedRequest != null) {
            String cachedRedirectUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(
                    request,
                    response,
                    rq.getAlertStorageRedirectUri(AlertType.INFO, nickname + "님, 환영합니다.", cachedRedirectUrl)
            );
            
        } else {
            redirectStrategy.sendRedirect(
                    request,
                    response,
                    rq.getAlertStorageRedirectUri(AlertType.INFO, nickname + "님, 환영합니다.", "/")
            );
        }
    }
}
