package org.pageflow.base.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.infra.jwt.token.AccessToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    private final JwtProvider jwtProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 토큰 추출
        Optional<String> accessTokenOptional = resolveToken(request);
        
        // 토큰 존재성 확인
        if (accessTokenOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 토큰 파싱
        AccessToken accessToken = jwtProvider.parseAccessToken(accessTokenOptional.get());
        
        // principal 작성
        UserDetails principal = new PrincipalContext(
                accessToken.getUID(),
                accessToken.getUsername(),
                "",
                accessToken.getRole()
        );
        
        // authentication 작성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "",
                RoleType.getAuthorities(accessToken.getRole())
        );

        // authentication 할당
        SecurityContextHolder.getContext().setAuthentication(authentication);
    
        filterChain.doFilter(request, response);
    }

    
    private Optional<String> resolveToken(HttpServletRequest request) {
        
        String token = null;
        
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            token = bearerToken.split(" ")[1].trim();
        }
        
        return Optional.ofNullable(token);
    }
}
