package org.pageflow.domain.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.constants.UserFetchDepth;
import org.pageflow.domain.user.dto.WebLoginRequest;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.entity.RefreshToken;
import org.pageflow.domain.user.model.token.AccessToken;
import org.pageflow.domain.user.model.token.AuthTokens;
import org.pageflow.domain.user.model.user.AggregateUser;
import org.pageflow.domain.user.service.DefaultUserService;
import org.pageflow.domain.user.service.UserApplication;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.exception.business.code.SessionCode;
import org.pageflow.global.exception.business.exception.BizException;
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
    private final DefaultUserService defaultUserService;
    private final RequestContext requestContext;
    private final CustomProps customProps;
    private final JwtProvider jwtProvider;
    
    
    /**
     * 일반적인 form 로그인 매핑
     * @param loginReq username, password
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @PostMapping("/login")
    public WebLoginResp webLogin(@Valid @RequestBody WebLoginRequest loginReq) {
        
        // 로그인 -> Access, Refresh 토큰 발급
        AuthTokens authTokens = userApp.login(loginReq.getUsername(), loginReq.getPassword());
        
        // 쿠키 설정 및 할당
        Cookie rfTknUUID = new Cookie(RefreshToken.COOKIE_NAME, authTokens.getRefreshToken().getId());
        rfTknUUID.setPath("/_pageflow/api/refresh");
        rfTknUUID.setHttpOnly(true); // JS에서 접근 불가
        rfTknUUID.setSecure(false); // HTTPS에서만 전송
        rfTknUUID.setMaxAge(60 * 60 * 24 * customProps.site().refreshTokenExpireDays()); // 30일
        requestContext.setCookie(rfTknUUID);
        
        // 사용자 정보 불러옴
        AggregateUser user = defaultUserService.fetchUser(
                authTokens.getAccessToken().getUID(), UserFetchDepth.FULL
        );
        Profile profile = user.getProfile();
        Account account = user.getAccount();
        
        // RETURN
        return WebLoginResp.builder()
                .accessToken(AccessTokenResp.builder()
                        .compact(authTokens.getAccessToken().getCompact())
                        .expiredAt(authTokens.getAccessToken().getExp().getTime())
                        .build()
                )
                .user(User.builder()
                        .UID(profile.getUID())
                        .username(account.getUsername())
                        .email(account.getEmail())
                        .penname(profile.getPenname())
                        .build()
                )
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
    public AuthTokens oauth2Login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return userApp.login(username, password);
    }
    
    /**
     * refreshToken으로 새로운 accessToken을 발급한다.
     */
    @Operation(summary = "compact 재발급", description = "refreshToken을 받아서, accessToken을 재발급")
    @PostMapping("/refresh") // 멱등성을 성립하지 않는 요청이라 Post임
    public AccessTokenResp refresh() {
        return requestContext.getCookie(RefreshToken.COOKIE_NAME)
            // 쿠키 존재
            .map(refreshTokenId -> {
                AccessToken accessToken = userApp.refresh(refreshTokenId.getValue());
                return AccessTokenResp.builder()
                        .compact(accessToken.getCompact())
                        .expiredAt(accessToken.getExp().getTime())
                        .build();
            })// 쿠키 존재하지 않음
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
    
    
    @Builder public record WebLoginResp(AccessTokenResp accessToken, User user){}
    @Builder record AccessTokenResp(String compact, long expiredAt){}
    @Builder record User(Long UID, String username, String email, String penname){}
}
