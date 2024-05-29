package org.pageflow.boundedcontext.auth.adapter.in.web;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.application.dto.Principal;
import org.pageflow.boundedcontext.auth.port.in.TokenUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;



@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    
    public static final String BEARER = "Bearer ";
    private final TokenUseCase useCase;
    
    @Override
    @SneakyThrows({ServletException.class, IOException.class})
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ){
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        Optional<String> accessTokenOp = resolveTokenIfExist(request);

        if(accessTokenOp.isPresent()) {
            Principal.Session principal = useCase.extractPrincipalFromAccessToken(accessTokenOp.get());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getRole().toAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // DB를 조회하지는 않기 때문에, DB에 사용자가 실제로 있는지 없는지는 모른다.
            log.debug("JWT AccessToken 인증 완료된 사용자: UID({}), ROLE({})", principal.getUid().toLong(), principal.getRole());
        } else {
            log.debug("인증되지 않은 사용자입니다: AccessToken 토큰 존재하지 않음");
        }
        filterChain.doFilter(request, response);
    }


    private Optional<String> resolveTokenIfExist(HttpServletRequest request) {
        String token = null;
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            token = bearerToken.split(" ")[1].trim();
        }
        return Optional.ofNullable(token);
    }
}
