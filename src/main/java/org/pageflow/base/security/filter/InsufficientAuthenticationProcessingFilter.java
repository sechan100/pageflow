package org.pageflow.base.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * spring security 상으로는 인증된 사용자의 인증을 다시한번 검증하는 필터.
 */
public class InsufficientAuthenticationProcessingFilter extends OncePerRequestFilter {

    /**
     * 커스텀으로 구현한 Authentication의 Principal의 UserSession role이 ROLE_ANONYMOUS인 경우, 인증을 해제한다.
     * 해제된 인증은, AnonymousAuthenticationFilter에서 체크하여 AnonymousAuthenticationToken을 생성한다.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String userRole = ((PrincipalContext) authentication.getPrincipal()).getUserSession().getRole();
            if (userRole.equals("ROLE_ANONYMOUS")) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        filterChain.doFilter(request, response);
    }

}
