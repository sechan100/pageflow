package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.InvalidField;
import org.pageflow.user.adapter.in.auth.LoginTokenEndpointForward;
import org.pageflow.user.adapter.in.auth.form.LoginUri;
import org.pageflow.user.adapter.in.res.AccessTokenRes;
import org.pageflow.user.adapter.in.res.SessionInfoRes;
import org.pageflow.user.dto.AccountDto;
import org.pageflow.user.dto.SessionUserDto;
import org.pageflow.user.dto.token.AccessTokenDto;
import org.pageflow.user.dto.token.AuthTokens;
import org.pageflow.user.dto.token.RefreshTokenDto;
import org.pageflow.user.port.in.IssueSessionCmd;
import org.pageflow.user.port.in.SessionUseCase;
import org.pageflow.user.port.out.LoadSessionUserPort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;


/**
 * @see org.pageflow.user.application.config.SecurityConfig
 * @author : sechan
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "auth", description = "인증관련")
public class AuthWebAdapter {
  public static final String SESSION_ID_COOKIE_PATH = "/auth";
  public static final String SESSION_ID_COOKIE_NAME = "PAGEFLOW_SESSION_IDENTIFIER";

  private final ApplicationProperties props;
  private final RequestContext rqrxt;
  private final SessionUseCase sessionUseCase;
  private final LoadSessionUserPort loadSessionUserPort;


  /**
   * 실제로 호출되지는 않는다. SpringSecurity Form 로그인으로 처리된다.
   * 문서화를 위한 endpoint
   */
  @PostMapping(LoginUri.SPRING_SECURITY_FORM_LOGIN_URI)
  @Operation(summary = "로그인", description = "로그인을 요청하고, accessToken을 발급한다.")
  private AccessTokenRes SpringSecurityLogin(String username, String password) {
    throw new UnsupportedOperationException("Spring Security에서 제공");
  }

  /**
   * SpringSecurity에서 form, 또는 OAuth2와 같은 다양한 방식을 통해서 로그인에 성공한 경우, 사용자에게 토큰을 반환하기 위해서
   * forward하는 endpoint.
   * 외부에서 직접 접근은 불가능하다.
   */
  @Hidden
  @RequestMapping(LoginTokenEndpointForward.LOGIN_TOKEN_URI)
  public AccessTokenRes login() {
    AccountDto account = LoginTokenEndpointForward.getForwardedAccount(rqrxt.getRequest());
    // LOGIN
    IssueSessionCmd cmd = new IssueSessionCmd(account.getUid());
    AuthTokens authTokens = sessionUseCase.issueSession(cmd);
    RefreshTokenDto rt = authTokens.getRefreshToken();
    AccessTokenDto at = authTokens.getAccessToken();

    // 쿠키 생성
    String sessionId = rt.getSessionId().toString();
    Cookie sessionIdCookie = new Cookie(SESSION_ID_COOKIE_NAME, sessionId);
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
    rqrxt.setCookie(sessionIdCookie);

    return new AccessTokenRes(
      at.getCompact(),
      at.getExp().toEpochMilli()
    );
  }

  @PostMapping("/auth/refresh")
  @Operation(summary = "accessToken 재발급", description = "session id cookie를 받아서 accessToken을 재발급한다.")
  public AccessTokenRes refresh() {
    UUID sessionId = _getSessionIdFromCookie();
    AccessTokenDto at = sessionUseCase.refresh(sessionId);

    return new AccessTokenRes(
      at.getCompact(),
      at.getExp().getEpochSecond()
    );
  }

  @PostMapping("/auth/logout")
  @Operation(summary = "로그아웃", description = "쿠키로 전달된 sessionId를 받아서, 해당 세션을 무효화")
  public void logout() {
    UUID sessionId = this._getSessionIdFromCookie();
    sessionUseCase.logout(sessionId);
    rqrxt.removeCookie(SESSION_ID_COOKIE_NAME);
  }

  @GetMapping("/user/session")
  @Operation(summary = "세션정보 가져오기", description = "accessToken에 저장된 UID를 기반으로 사용자의 세션 정보를 조회")
  public SessionInfoRes getSession() {
    UID uid = rqrxt.getUid();
    SessionUserDto dto = loadSessionUserPort.load(uid);
    var user = SessionInfoRes.SessionUser.from(dto);
    return new SessionInfoRes(user);
  }


  private UUID _getSessionIdFromCookie() {
    Optional<Cookie> cookieOpt = rqrxt.getCookie(SESSION_ID_COOKIE_NAME);
    if(cookieOpt.isEmpty()){
      Result<InvalidField> cookieInvalidResult = Result.of(CommonCode.INVALID_COOKIE,
        InvalidField.builder()
          .field(SESSION_ID_COOKIE_NAME)
          .reason(FieldReason.NULL)
          .message(FieldReason.NULL.getDefaultMessage())
          .build()
      );
      throw new ProcessResultException(cookieInvalidResult);
    }
    return UUID.fromString(cookieOpt.get().getValue());
  }

}
