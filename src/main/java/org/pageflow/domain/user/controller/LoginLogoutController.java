package org.pageflow.domain.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.RefreshToken;
import org.pageflow.domain.user.model.dto.WebLoginRequest;
import org.pageflow.domain.user.service.UserApplication;
import org.pageflow.domain.user.service.UserApplication.*;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.exception.business.code.SessionCode;
import org.pageflow.global.exception.business.exception.BizException;
import org.pageflow.global.request.RequestContext;
import org.pageflow.infra.jwt.dto.AccessTokenDto;
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
    private final CustomProps customProps;
    private final JwtProvider jwtProvider;
    
    
    /**
     * 일반적인 form 로그인 매핑
     * @param loginReq username, password
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @PostMapping("/login")
    public AccessTokenDto webLogin(@Valid @RequestBody WebLoginRequest loginReq) {
        LoginTokens result = userApp.login(loginReq.getUsername(), loginReq.getPassword());
        
        Cookie refreshTokenUUID = new Cookie(
                RefreshToken.COOKIE_NAME, result.refreshTokenUUID()
        );
        refreshTokenUUID.setPath("/_pageflow/api/refresh");
        refreshTokenUUID.setHttpOnly(true); // JS에서 접근 불가
        refreshTokenUUID.setSecure(false); // HTTPS에서만 전송
        refreshTokenUUID.setMaxAge(60 * 60 * 24 * customProps.site().refreshTokenExpireDays()); // 30일
        
        // 쿠키 할당
        requestContext.setCookie(refreshTokenUUID);
        
        // RETURN
        return AccessTokenDto.builder()
                .accessToken(result.accessToken())
                .expiredAt(result.accessTokenExpiredAt())
                .build();
    }
    
    
    /**
     * OAuth2로 접근하는 요청을 포워딩하여 로그인처리. <br>
     * authorization_code를 포함한 인가요청을 해당 매핑으로 포워딩하여 로그인을 처리한다.
     * @param username
     * @param password
     */
    @Hidden
    @GetMapping("/internal/login")
    public UserApplication.LoginTokens oauth2Login(
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
    public AccessTokenDto refresh() {
        return requestContext.getCookie(RefreshToken.COOKIE_NAME)
                // 쿠키 존재
                .map(refreshTokenId -> userApp.refresh(refreshTokenId.getValue()))
                
                // 쿠키 존재하지 않음
                .orElseThrow(()-> BizException.builder()
                        .code(SessionCode.TOKEN_NOT_FOUND)
                        .message("'" + RefreshToken.COOKIE_NAME + "' 이름을 가진 쿠키가 존재하지 않습니다.")
                        .build()
                );
        
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
