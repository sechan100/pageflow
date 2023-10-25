package org.pageflow.base.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
    
    @Value("${custom.site.login-form-uri}")
    private String loginFormUrl;
    
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        
        String queryString;
        
        // UsernameNotFoundException
        if(exception instanceof UsernameNotFoundException) {
            
            queryString = "?error=username";
            response.sendRedirect(loginFormUrl + queryString);
            
        // BadCredentialsException
        } else if(exception instanceof BadCredentialsException) {
            
            queryString = "?error=password";
            response.sendRedirect(loginFormUrl + queryString);
            
        }
        
    }
}