package org.pageflow.boundedcontext.auth.adapter.in.web;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.application.acl.LoadSessionUserAcl;
import org.pageflow.boundedcontext.auth.application.dto.Token;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.auth.port.in.LoginCmd;
import org.pageflow.boundedcontext.auth.port.in.SessionUseCase;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.ResDataTypes;
import org.pageflow.global.api.code.ApiCode4;
import org.pageflow.global.api.exception.ApiException;
import org.pageflow.global.filter.UriPrefix;
import org.pageflow.global.property.AppProps;
import org.pageflow.shared.annotation.web.Get;
import org.pageflow.shared.annotation.web.Post;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class AuthWebAdapter {
    private static final String SESSION_ID_COOKIE_PATH = "/auth/session";
    private static final String SESSION_ID_COOKIE_NAME = "PAGEFLOE_SESSION_IDENTIFIER";

    private final AppProps props;
    private final RequestContext requestContext;
    private final SessionUseCase sessionUseCase;
    private final LoadSessionUserAcl loadSessionUserAcl;


    public static final String SPRING_SECURITY_FORM_LOGIN_URI = "/auth/login";
    @Post(SPRING_SECURITY_FORM_LOGIN_URI)
    @Operation(summary = "로그인", description = "로그인을 요청하고, accessToken을 발급한다.")
    private Res.AccessToken SpringSecurityLogin(String username, String password) {
        throw new UnsupportedOperationException("Spring Security에서 제공");
    }

    public static final String LOGIN_URI = UriPrefix.PRIVATE + "/auth/login/tokens";
    public static final String ACCOUNT_REQUEST_ATTR_KEY = "authedAccount";
    @Hidden
    @RequestMapping(LOGIN_URI)
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

        return new Res.AccessToken(
            at.getCompact(),
            at.getExp().toEpochMilli()
        );
    }

    @Post("/auth/session/refresh")
    @Operation(summary = "accessToken 재발급", description = "session id cookie를 받아서 accessToken을 재발급한다.")
    public Res.AccessToken refresh(HttpServletRequest request) {
        Optional<Cookie> rtCookieOp = requestContext.getCookie(SESSION_ID_COOKIE_NAME);
        // TODO: 나중에 MissingRequestCookieException으로 한번에 받아서 처리하는 코드로 변경
        if(rtCookieOp.isEmpty()){
            throw new ApiException(
                ApiCode4.REQUIRED_COOKIE_NOT_FOUND,
                new ResDataTypes.FieldName(SESSION_ID_COOKIE_NAME)
            );
        }

        // REFRESH
        Token.AccessTokenDto at = sessionUseCase.refresh(SessionId.from(rtCookieOp.get().getValue()));

        return new Res.AccessToken(
            at.getCompact(),
            at.getExp().getEpochSecond()
        );
    }

    @Post("/auth/session/logout")
    @Operation(summary = "로그아웃", description = "쿠키로 전달된 sessionId를 받아서, 해당 세션을 무효화")
    public void logout() {
        requestContext.getCookie(SESSION_ID_COOKIE_NAME)
            .ifPresent(cookie -> {
                sessionUseCase.logout(SessionId.from(cookie.getValue()));
                requestContext.removeCookie(SESSION_ID_COOKIE_NAME);
            });
    }

    @Get("/auth/session/info")
    @Operation(summary = "세션정보 가져오기", description = "accessToken에 저장된 UID를 기반으로 사용자의 세션 정보를 조회")
    public Res.SessionInfo getSession(){
        UID uid = requestContext.getUid();
        UserDto.Session sessionUser = loadSessionUserAcl.loadSessionUser(uid);
        return new Res.SessionInfo(
            sessionUser
        );
    }


}
