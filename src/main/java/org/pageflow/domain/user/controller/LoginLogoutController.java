package org.pageflow.domain.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.RefreshToken;
import org.pageflow.domain.user.model.dto.WebLoginRequest;
import org.pageflow.domain.user.service.UserApplication;
import org.pageflow.global.request.RequestContext;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.springframework.web.bind.annotation.*;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class LoginLogoutController {
    
    private final UserApplication userApp;
    private final RequestContext requestContext;
    private final JwtProvider jwtProvider;
    
    
    /**
     * 일반적인 form 로그인 매핑
     * @param loginReq username, password
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @PostMapping("/login")
    public UserApplication.ClientAspectAuthResults webLogin(@Valid @RequestBody WebLoginRequest loginReq) {
        return userApp.login(loginReq.getUsername(), loginReq.getPassword());
    }
    
    /**
     * OAuth2로 접근하는 요청을 포워딩하여 로그인처리. <br>
     * authorization_code를 포함한 인가요청을 해당 매핑으로 포워딩하여 로그인을 처리한다.
     * @param username
     * @param password
     */
    @Hidden
    @GetMapping("/internal/login")
    public UserApplication.ClientAspectAuthResults oauth2Login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return userApp.login(username, password);
    }
    
    /**
     * refreshToken으로 새로운 accessToken을 발급한다.
     */
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 받아서, accessToken을 재발급")
    @PostMapping("/refresh") // 멱등성을 성립하지 않는 요청이라 Post임
    public JwtProvider.AccessTokenReturn refresh() {
        return requestContext.getCookie(RefreshToken.COOKIE_NAME)
                .map(refreshTokenId -> userApp.refresh(refreshTokenId.getValue()))
                .orElseThrow();
        
    }
    
    /**
     * 리프레시 토큰을 제거
     */
    @Operation(summary = "로그아웃", description = "쿠키로 전달된 refreshTokenId를 받아서, 해당 세션을 무효화")
    @PostMapping("/logout")
    public void logout() {
        //TODO: refreshToken을 정상적으로 삭제하지 못한 경우의 동작
        requestContext.getCookie(RefreshToken.COOKIE_NAME)
                .ifPresent(refreshTokenId -> userApp.logout((refreshTokenId.getValue())));
        //TODO: 쿠키가 존재하지 않는 경우의 동작...
    }
    
}
