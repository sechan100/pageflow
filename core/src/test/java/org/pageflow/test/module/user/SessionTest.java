//package org.pageflow.test.module.user;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.pageflow.common.result.code.CommonCode;
//import org.pageflow.test.e2e.ApiFactory;
//import org.pageflow.test.e2e.TestRes;
//import org.pageflow.user.application.UserCode;
//
/// **
// * @author : sechan
// */
//@RequiredArgsConstructor
//public class SessionTest {
//  private final ApiTestDataCreator apiTestDataCreator;
//  private final ApiFactory apiFactory;
//  private final TestAccessTokenIssuer testAccessTokenIssuer;
//
//  @Test
//  @DisplayName("로그인 & 로그아웃")
//  void loginLogout() {
//    API guestApi = apiFactory.guest();
//
//    // Guest가 세션을 조회하면 실패
//    guestApi.get("/user/session").is(CommonCode.LOGIN_REQUIRED);
//
//    // 로그인 후에 세션을 조회하면 성공한다.
//    String username = "user1";
//    apiTestDataCreator.createUser(username);
//    API userApi = apiFactory.user(username, username);
//    userApi.get("/user/session").isSuccess();
//
//    // 로그아웃 성공
//    userApi.post("/auth/logout", null).isSuccess();
//  }
//
//  @Test
//  @DisplayName("refresh 테스트")
//  void accessTokenExpiredAndRefresh() {
//    // 로그인
//    String username = "user1";
//    apiTestDataCreator.createUser(username);
//    API userApi = apiFactory.user(username, username);
//
//    // 만약 토큰이 만료된 경우, session 조회 실패
//    String expiredAccessToken = testAccessTokenIssuer.issueImmediatelyExpireToken(username);
//    API expiredApi = apiFactory.guest();
//    expiredApi.setAccessToken(expiredAccessToken);
//    expiredApi.setSessionIdCookie(userApi.getSessionIdCookie());
//    expiredApi.get("/user/session").is(UserCode.ACCESS_TOKEN_EXPIRED);
//
//    // refresh해서 새로운 토큰을 발급
//    expiredApi.clearAccessToken();
//    TestRes refreshRes = expiredApi.post("/auth/refresh", null);
//    refreshRes.isSuccess();
//    String newAccessToken = refreshRes.getData().get("compact").asText();
//
//    // 갱신된 accessToken으로 다시 session 조회
//    API refreshedApi = apiFactory.guest();
//    refreshedApi.setAccessToken(newAccessToken);
//    refreshedApi.setSessionIdCookie(userApi.getSessionIdCookie());
//    refreshedApi.get("/user/session").isSuccess();
//
//    // 로그아웃 후, refresh 요청시 실패
//    refreshedApi.post("/auth/logout", null).isSuccess();
//    refreshedApi.clearAccessToken();
//    refreshedApi.post("/auth/refresh", null).is(UserCode.SESSION_EXPIRED);
//  }
//
//
//}
