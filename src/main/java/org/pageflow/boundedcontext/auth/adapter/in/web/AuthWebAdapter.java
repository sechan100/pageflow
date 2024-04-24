package org.pageflow.boundedcontext.auth.adapter.in.web;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.application.dto.Token;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.auth.port.in.LoginCmd;
import org.pageflow.boundedcontext.auth.port.in.SessionUseCase;
import org.pageflow.global.api.ApiAccess;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.code.Code1;
import org.pageflow.global.filter.UriPrefix;
import org.pageflow.global.property.AppProps;
import org.pageflow.shared.annotation.WebAdapter;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;


/**
 * @author : sechan
 */
@WebAdapter
@RequiredArgsConstructor
public class AuthWebAdapter {
    private static final String SESSION_ID_COOKIE_PATH = "/auth/session";
    private static final String SESSION_ID_COOKIE_NAME = "PAGEFLOE_SESSION_IDENTIFIER";

    private final AppProps props;
    private final RequestContext requestContext;
    private final SessionUseCase sessionUseCase;


    @Operation(summary = "로그인", description = "로그인을 요청하고, accessToken을 발급한다.")
    @PostMapping("/auth/login")
    private Res.AccessToken SpringSecurityLogin(String username, String password) {
        throw new UnsupportedOperationException("Spring Security에서 제공");
    }

    public static final String LOGIN_PATH = UriPrefix.PRIVATE + "/auth/login/tokens";
    public static final String ACCOUNT_REQUEST_ATTR_KEY = "authedAccount";
    @Hidden
    @RequestMapping(LOGIN_PATH)
    public Res.AccessToken login() {
        Account account = requestContext.getRequestAttr(ACCOUNT_REQUEST_ATTR_KEY);
        // LOGIN
        Token.AuthTokens authTokens = sessionUseCase.login(new LoginCmd(account));
        Token.RefreshTokenDto rt = authTokens.getRefreshToken();
        Token.AccessTokenDto at = authTokens.getAccessToken();

        // 쿠키 생성
        String sessionId = rt.getSessionId().toString();
        Cookie sessionIdCookie = new Cookie(SESSION_ID_COOKIE_NAME,sessionId );
        sessionIdCookie.setPath(props.site.clientProxyPrefix + SESSION_ID_COOKIE_PATH);
        sessionIdCookie.setHttpOnly(true); // JS에서 접근 불가

        // HTTPS에서만 전송여부 설정
        boolean isHttps = "https".equals(props.site.protocol);
        sessionIdCookie.setSecure(isHttps);

        // 만료시간 설정
        @SuppressWarnings("NumericCastThatLosesPrecision")
        int refreshTokenEpochSecExp = (int) rt.getExp().getEpochSecond();
        sessionIdCookie.setMaxAge(refreshTokenEpochSecExp);

        // 할당
        requestContext.setCookie(sessionIdCookie);

        return Res.AccessToken.builder()
                .compact(at.getCompact())
                .exp(at.getExp().getEpochSecond())
                .build();
    }

    @Secured(ApiAccess.USER)
    @Operation(summary = "accessToken 재발급", description = "session id cookie를 받아서 accessToken을 재발급한다.")
    @PostMapping("/auth/session/refresh")
    public Res.AccessToken refresh(HttpServletRequest request) {
        Optional<Cookie> rtCookieOp = requestContext.getCookie(SESSION_ID_COOKIE_NAME);
        if(rtCookieOp.isEmpty()){
            throw Code1.SESSION_ID_COOKIE_NOT_FOUND.fire();
        }

        // REFRESH
        Token.AccessTokenDto at = sessionUseCase.refresh(SessionId.from(rtCookieOp.get().getValue()));

        return Res.AccessToken.builder()
            .compact(at.getCompact())
            .exp(at.getExp().getEpochSecond())
            .build();
    }

    @Secured(ApiAccess.USER) // USER 권한이 있어야만 로그아웃 가능
    @Operation(summary = "로그아웃", description = "쿠키로 전달된 sessionId를 받아서, 해당 세션을 무효화")
    @PostMapping("/auth/session/logout")
    public void logout() {
        requestContext.getCookie(SESSION_ID_COOKIE_NAME).ifPresent(cookie ->
        {
            sessionUseCase.logout(SessionId.from(cookie.getValue()));
            requestContext.removeCookie(SESSION_ID_COOKIE_NAME);
        });
    }

}
