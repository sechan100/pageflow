package org.pageflow.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.global.request.RequestContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestContext requestContext;
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        UserDto userDto = ((PrincipalContext) authentication.getPrincipal()).getUserDto();
//        String nickname = userDto.getPenname();
//
//        SavedRequest savedRequest = requestCache.getRequest(request, response);
//
//        if (savedRequest != null) {
//            String cachedRedirectUrl = savedRequest.getRedirectUrl();
//            redirectStrategy.sendRedirect(
//                    request,
//                    response,
//                    cachedRedirectUrl
//            );
//
//        } else {
//            redirectStrategy.sendRedirect(
//                    request,
//                    response,
//                    "/"
//            );
//        }
    }
}
