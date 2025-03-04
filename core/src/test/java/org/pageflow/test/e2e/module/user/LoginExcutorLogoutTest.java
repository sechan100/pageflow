package org.pageflow.test.e2e.module.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.test.e2e.config.PageflowIntegrationTest;
import org.pageflow.test.e2e.module.user.dto.TUser;
import org.pageflow.test.e2e.module.user.shared.SignupExcetuor;
import org.pageflow.test.e2e.module.user.shared.TestAccessTokenIssuer;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.pageflow.test.e2e.shared.ApiResponseWrapper;
import org.pageflow.user.application.UserCode;

import java.util.Map;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class LoginExcutorLogoutTest {
  private final SignupExcetuor signupExcetuor;
  private final ApiFactory apiFactory;
  private final TestAccessTokenIssuer testAccessTokenIssuer;

  @Test
  @DisplayName("로그인 & 로그아웃")
  void loginLogout() {
    API guestApi = apiFactory.guest();

    // Guest가 세션을 조회하면 실패
    guestApi.get("/user/session").is(CommonCode.LOGIN_REQUIRED);

    // 로그인 후에 세션을 조회하면 성공한다.
    TUser user = signupExcetuor.signup();
    API userApi = apiFactory.user(user.getUsername(), user.getPassword());
    userApi.get("/user/session").isSuccess();

    // 로그아웃 성공
    userApi.post("/auth/logout", null).isSuccess();

    // 로그아웃 재시도하면 실패
    userApi.post("/auth/logout", null).is(UserCode.ALREADY_LOGOUT);
  }

  @Test
  @DisplayName("refresh 테스트")
  void accessTokenExpiredAndRefresh() {
    API guestApi = apiFactory.guest();
    // 로그인
    TUser user = signupExcetuor.signup();
    String username = user.getUsername();
    API userApi = apiFactory.user(username, user.getPassword());

    // 만약 토큰이 만료된 경우, session 조회 실패
    String expiredAccessToken = testAccessTokenIssuer.issueImmediatelyExpireToken(username);
    API expiredApi = apiFactory.guest();
    expiredApi.setAccessToken(expiredAccessToken);
    expiredApi.setSessionIdCookie(userApi.getSessionIdCookie());
    expiredApi.get("/user/session").is(UserCode.ACCESS_TOKEN_EXPIRED);

    // refresh해서 새로운 토큰을 발급
    expiredApi.clearAccessToken();
    ApiResponseWrapper refreshRes = expiredApi.post("/auth/refresh", null);
    refreshRes.isSuccess();
    Map<String, String> data = (Map<String, String>) refreshRes.getApiResponse().getData();
    assert data != null;
    String newAccessToken = data.get("compact");

    // 갱신된 accessToken으로 다시 session 조회
    API refreshedApi = apiFactory.guest();
    refreshedApi.setAccessToken(newAccessToken);
    refreshedApi.setSessionIdCookie(userApi.getSessionIdCookie());
    refreshedApi.get("/user/session").isSuccess();

    // 로그아웃 후, refresh 요청시 실패
    refreshedApi.post("/auth/logout", null).isSuccess();
    refreshedApi.clearAccessToken();
    refreshedApi.post("/auth/refresh", null).is(UserCode.SESSION_EXPIRED);
  }


}
