package org.pageflow.test.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.core.PageflowApplication;
import org.pageflow.test.TestModuleConfig;
import org.pageflow.test.api.API;
import org.pageflow.test.api.ApiFactory;
import org.pageflow.test.api.ResTestWrapper;
import org.pageflow.test.user.utils.Login;
import org.pageflow.test.user.utils.LoginResult;
import org.pageflow.test.user.utils.Signup;
import org.pageflow.test.user.utils.TestAccessTokenIssuer;
import org.pageflow.user.adapter.in.res.UserRes;
import org.pageflow.user.application.UserCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

/**
 * @author : sechan
 */
@SpringBootTest(classes = PageflowApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestModuleConfig.class)
@ActiveProfiles("test")
public class LoginLogoutTest {
  @Autowired
  private Login login;
  @Autowired
  private Signup signup;
  @Autowired
  private ApiFactory apiFactory;
  @Autowired
  private TestAccessTokenIssuer testAccessTokenIssuer;

  @Test
  @DisplayName("/login")
  void loginLogout() {
    API api = apiFactory.createAPI();

    // 그냥 session을 조회하면 실패
    api.get("/auth/session/info").is(CommonCode.LOGIN_REQUIRED);

    // 로그인
    UserRes user = signup.signup();
    String username = user.getUsername();
    var loginResult = login.login(username);

    // 로그인 후 session 조회
    API authenticatedApi = apiFactory.createAuthenticated(loginResult);
    var session = authenticatedApi.get("/auth/session/info");
    session.isSuccess();

    // 로그아웃
    authenticatedApi.post("/auth/session/logout", null).isSuccess();

    // 로그아웃 재시도
    authenticatedApi.post("/auth/session/logout", null).is(UserCode.ALREADY_LOGOUT);
  }

  @Test
  @DisplayName("/auth/session/refresh")
  void accessTokenExpiredAndRefresh() {
    API anonymousApi = apiFactory.createAPI();
    // 로그인
    UserRes user = signup.signup();
    String username = user.getUsername();
    var loginRes = login.login(username);

    // 만료된 토큰 발행
    String expiredAccessToken = testAccessTokenIssuer.issueImmediatelyExpireToken(username);
    LoginResult expiredLoginResult = new LoginResult(expiredAccessToken, loginRes.getSessionIdCookie());

    // 만료 토큰으로 인증 api 생성 후, session 조회시 실패
    API expiredApi = apiFactory.createAuthenticated(expiredLoginResult);
    var sessionWithexpiredTokenRes = expiredApi.get("/auth/session/info");
    sessionWithexpiredTokenRes.is(UserCode.ACCESS_TOKEN_EXPIRED);

    // refresh 요청
    expiredApi.clearAccessToken();
    ResTestWrapper refreshRes = expiredApi.post("/auth/session/refresh", null);
    refreshRes.isSuccess();
    Map<String, String> data = (Map<String, String>) refreshRes.getApiResponse().getData();
    assert data != null;
    String newAccessToken = data.get("compact");

    // 갱신된 accessToken으로 session 조회
    LoginResult validLoginResult = new LoginResult(newAccessToken, loginRes.getSessionIdCookie());
    API authenticatedApi = apiFactory.createAuthenticated(validLoginResult);
    authenticatedApi.get("/auth/session/info").isSuccess();

    // 로그아웃 후, refresh 요청시 실패
    authenticatedApi.post("/auth/session/logout", null).isSuccess();
    authenticatedApi.clearAccessToken();
    authenticatedApi.post("/auth/session/refresh", null).is(UserCode.SESSION_EXPIRED);
  }


}
