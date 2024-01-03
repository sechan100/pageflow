package org.pageflow.domain.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.jwt.JwtProvider;
import org.pageflow.domain.user.model.dto.WebLoginRequest;
import org.pageflow.domain.user.model.dto.WebLogoutRequest;
import org.pageflow.domain.user.model.dto.WebRefreshRequest;
import org.pageflow.domain.user.model.token.AccessToken;
import org.pageflow.domain.user.model.token.RefreshToken;
import org.pageflow.domain.user.model.token.SessionToken;
import org.pageflow.domain.user.service.DefaultUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class LoginLogoutController {
    
    private final DefaultUserService defaultUserService;
    private final JwtProvider jwtProvider;
    
    
    /**
     * 일반적인 form 로그인 매핑
     * @param login username, password
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @PostMapping("/login")
    public TokenRes loginWithRequestBody(@Valid @RequestBody WebLoginRequest login) {
        Map result = defaultUserService.login(login.getUsername(), login.getPassword());
        return TokenRes.loginRes(result);
    }
    
    
    /**
     * OAuth2로 접근하는 요청을 포워딩하여 로그인처리. <br>
     * authorization_code를 포함한 인가요청을 해당 매핑으로 포워딩하여 로그인을 처리한다.
     * @param username
     * @param password
     */
    @Hidden
    @GetMapping("/internal/login")
    public TokenRes oauth2InternalLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        Map result = defaultUserService.login(username, password);
        return TokenRes.loginRes(result);
    }
    
    
    /**
     * refreshToken으로 새로운 accessToken을 발급한다.
     */
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 받아서, accessToken을 재발급")
    @PostMapping("/refresh")
    public AccessTokenRes refresh(@RequestBody WebRefreshRequest refresh) {
        
        AccessToken newAccessToken = defaultUserService.refresh(refresh.getRefreshToken());
        
        return new AccessTokenRes(
                newAccessToken.getToken(), // accessToken
                newAccessToken.getExp().getTime() // accessTokenExpiresIn
        );
    }
    
    
    /**
     * refreshToken을 받아서 세션을 지운다. 이미 만료된 세션이라면 아무런 행동도 하지 않는다.
     * @param logout refreshToken
     */
    @Operation(summary = "로그아웃", description = "refreshToken을 받아서, 해당 세션을 무효화")
    @PostMapping("/logout")
    public void logout(@RequestBody WebLogoutRequest logout) {
        defaultUserService.logout(logout.getRefreshToken());
    }
    
    
    public record TokenRes(String accessToken, long accessTokenExpiresIn, String refreshToken, long refreshTokenExpiresIn) {
        public static TokenRes loginRes(Map<String, SessionToken> tokens) {
            AccessToken accessToken = (AccessToken) tokens.get("accessToken");
            RefreshToken refreshToken = (RefreshToken) tokens.get("refreshToken");
            return new TokenRes(
                    accessToken.getToken(),
                    accessToken.getExp().getTime(),
                    refreshToken.getToken(),
                    refreshToken.getExp().getTime()
            );
        }
    }
    public record AccessTokenRes(String accessToken, long accessTokenExpiresIn){}
}
