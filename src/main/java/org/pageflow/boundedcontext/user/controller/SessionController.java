package org.pageflow.boundedcontext.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.pageflow.boundedcontext.user.constants.UserFetchDepth;
import org.pageflow.boundedcontext.user.domain.UserDomain;
import org.pageflow.boundedcontext.user.entity.RefreshToken;
import org.pageflow.boundedcontext.user.model.token.AccessToken;
import org.pageflow.boundedcontext.user.model.token.AuthTokens;
import org.pageflow.boundedcontext.user.model.user.AggregateUser;
import org.pageflow.boundedcontext.user.model.user.PublicUserInfo;
import org.pageflow.boundedcontext.user.service.$UserServiceUtil;
import org.pageflow.global.api.code.SessionCode;
import org.pageflow.global.api.code.exception.BizException;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.request.RequestContext;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.springframework.web.bind.annotation.*;


/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class SessionController {
    
    private final UserDomain userDomain;
    private final $UserServiceUtil userServiceUtil;
    private final RequestContext requestContext;
    private final CustomProps customProps;
    private final JwtProvider jwtProvider;
    
    
    /**
     * 일반적인 form 로그인 매핑
     * @param loginReq username, password
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @PostMapping("/user/login")
    public AccessTokenResp webLogin(@Valid @RequestBody WebLoginRequest loginReq) {
        // 로그인 -> Access, Refresh 토큰 발급
        AuthTokens authTokens = userDomain.formLogin(loginReq.username, loginReq.password);
        // 세션을 할당
        return allocateSession(authTokens);
    }
    
    
    /**
     * OAuth2로 접근하는 요청을 포워딩하여 로그인처리. <br>
     * authorization_code를 포함한 인가요청을 해당 매핑으로 포워딩하여 로그인을 처리한다.
     */
    @Hidden
    @GetMapping("/internal/user/oauth2/login")
    public AccessTokenResp oauth2Login(String username) {
        // 로그인 -> Access, Refresh 토큰 발급
        AuthTokens authTokens = userDomain.oauth2Login(username);
        // 세션을 할당
        return allocateSession(authTokens);
    }
    
    
    /**
     * 인증 토큰을 받아서, 쿠키에 세션을 할당하고, accessToken은 반환한다.
     * @param authTokens accessToken과 refreshToken
     * @return accessToken
     */
    private AccessTokenResp allocateSession(AuthTokens authTokens) {
        // 쿠키 설정 및 할당
        Cookie rfTknUUID = new Cookie(RefreshToken.COOKIE_NAME, authTokens.getRefreshToken().getId());
        rfTknUUID.setPath("/_pageflow/api/user/refresh");
        rfTknUUID.setHttpOnly(true); // JS에서 접근 불가
        rfTknUUID.setSecure(false); // HTTPS에서만 전송
        rfTknUUID.setMaxAge(60 * 60 * 24 * customProps.site().refreshTokenExpireDays()); // 30일
        requestContext.setCookie(rfTknUUID);
        
        // RETURN
        return AccessTokenResp.builder()
                .compact(authTokens.getAccessToken().getCompact())
                .expiredAt(authTokens.getAccessToken().getExp().getTime())
                .build();
    }
    
    
    /**
     * refreshToken으로 새로운 accessToken을 발급한다.
     */
    @Operation(summary = "compact 재발급", description = "refreshToken을 받아서, accessToken을 재발급")
    @PostMapping("/user/refresh") // 멱등성을 성립하지 않는 요청이라 Post임
    public AccessTokenResp refresh() {
        return requestContext.getCookie(RefreshToken.COOKIE_NAME)
            // 쿠키 존재
            .map(refreshTokenId -> {
                AccessToken accessToken = userDomain.refresh(refreshTokenId.getValue());
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
    @PostMapping("/user/logout")
    public void logout() {
        //TODO: refreshToken을 정상적으로 삭제하지 못한 경우의 동작
        requestContext.getCookie(RefreshToken.COOKIE_NAME)
                .ifPresent(refreshTokenId -> userDomain.logout((refreshTokenId.getValue())));
        //TODO: 쿠키가 존재하지 않는 경우의 동작...
    }
    
    
    @Operation(summary = "세션 정보", description = "현재 세션의 정보를 반환")
    @GetMapping("/user/session")
    public Session getSession() {
        
        AggregateUser userAggregate = userServiceUtil.fetchUser(requestContext.getUID(), UserFetchDepth.FULL);
        
        // 사용자 정보 객체 생성
        PublicUserInfo userInfo = PublicUserInfo.builder()
                .email(userAggregate.getAccount().getEmail())
                .penname(userAggregate.getProfile().getPenname())
                .UID(userAggregate.getProfile().getUID())
                .username(userAggregate.getAccount().getUsername())
                .build();
        
        return Session.builder()
                .user(userInfo)
                .build();
    }
    
    
    @Builder record WebLoginRequest(@NotBlank String username, @NotBlank String password){}
    @Builder record AccessTokenResp(String compact, long expiredAt){}
    @Builder record Session(PublicUserInfo user){}
}
