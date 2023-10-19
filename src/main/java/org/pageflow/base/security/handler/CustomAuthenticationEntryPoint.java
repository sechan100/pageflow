package org.pageflow.base.security.handler;

import io.sseg.base.request.Rq;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    
    public CustomAuthenticationEntryPoint(@Value("${custom.site.login-form-url}") String loginFormUrl, @Autowired Rq rq) {
        super(loginFormUrl);
        this.loginFormUrl = loginFormUrl;
        this.rq = rq;
    }
    
    private final Rq rq;
    private final String loginFormUrl;
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String redirectUri = request.getRequestURI();
        String queryString;
        
        // anonymous가 authenticated에 접근
        if(authException instanceof InsufficientAuthenticationException) {
            
            // LoginUrlAuthenticationEntryPoint에 loginFormUrl을 재설정하게 해주는 메소드가 없기 때문에, 새로 만들어서 error 쿼리 스트링을 가진 loginFormUrl을 주고 commence.
            new LoginUrlAuthenticationEntryPoint(loginFormUrl + "?error=anonymous").commence(request, response, authException);
        }
    }
}