package org.pageflow.test.e2e.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.pageflow.test.e2e.shared.PageflowIntegrationTest;
import org.pageflow.test.e2e.shared.ResTestWrapper;
import org.pageflow.test.e2e.user.shared.LoginExcutor;
import org.pageflow.test.e2e.user.shared.LoginResult;
import org.pageflow.test.e2e.user.shared.SignupExcetuor;
import org.pageflow.test.e2e.user.shared.TestAccessTokenIssuer;
import org.pageflow.user.adapter.in.res.UserRes;
import org.pageflow.user.application.UserCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
public class LoginExcutorLogoutTest {
  @Autowired
  private LoginExcutor loginExcutor;
  @Autowired
  private SignupExcetuor signupExcetuor;
  @Autowired
  private ApiFactory apiFactory;
  @Autowired
  private TestAccessTokenIssuer testAccessTokenIssuer;

  @Test
  @DisplayName("로그인 & 로그아웃")
  void loginLogout() {
    API api = apiFactory.createAPI();

    // 그냥 session을 조회하면 실패
    api.get("/user/session").is(CommonCode.LOGIN_REQUIRED);

    // 로그인
    UserRes user = signupExcetuor.signup();
    String username = user.getUsername();
    var loginResult = loginExcutor.login(username);

    // 로그인 후 session 조회
    API authenticatedApi = apiFactory.createAuthenticated(loginResult);
    var session = authenticatedApi.get("/user/session");
    session.isSuccess();

    // 로그아웃
    authenticatedApi.post("/auth/logout", null).isSuccess();

    // 로그아웃 재시도
    authenticatedApi.post("/auth/logout", null).is(UserCode.ALREADY_LOGOUT);
  }

  @Test
  @DisplayName("refresh 테스트")
  void accessTokenExpiredAndRefresh() {
    API anonymousApi = apiFactory.createAPI();
    // 로그인
    UserRes user = signupExcetuor.signup();
    String username = user.getUsername();
    var loginRes = loginExcutor.login(username);

    // 만료된 토큰 발행
    String expiredAccessToken = testAccessTokenIssuer.issueImmediatelyExpireToken(username);
    LoginResult expiredLoginResult = new LoginResult(expiredAccessToken, loginRes.getSessionIdCookie());

    // 만료 토큰으로 인증 api 생성 후, session 조회시 실패
    API expiredApi = apiFactory.createAuthenticated(expiredLoginResult);
    var sessionWithexpiredTokenRes = expiredApi.get("/user/session");
    sessionWithexpiredTokenRes.is(UserCode.ACCESS_TOKEN_EXPIRED);

    // refresh 요청
    expiredApi.clearAccessToken();
    ResTestWrapper refreshRes = expiredApi.post("/auth/refresh", null);
    refreshRes.isSuccess();
    Map<String, String> data = (Map<String, String>) refreshRes.getApiResponse().getData();
    assert data != null;
    String newAccessToken = data.get("compact");

    // 갱신된 accessToken으로 session 조회
    LoginResult validLoginResult = new LoginResult(newAccessToken, loginRes.getSessionIdCookie());
    API authenticatedApi = apiFactory.createAuthenticated(validLoginResult);
    authenticatedApi.get("/user/session").isSuccess();

    // 로그아웃 후, refresh 요청시 실패
    authenticatedApi.post("/auth/logout", null).isSuccess();
    authenticatedApi.clearAccessToken();
    authenticatedApi.post("/auth/refresh", null).is(UserCode.SESSION_EXPIRED);
  }


}
