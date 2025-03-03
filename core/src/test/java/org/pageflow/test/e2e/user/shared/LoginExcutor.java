package org.pageflow.test.e2e.user.shared;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class LoginExcutor {
  private final ApiFactory apiFactory;

  public LoginResult login(String username) {
    API api = apiFactory.createAPI();

    // 로그인 요청 전송
    TestRestTemplate delegate = api.getDelegate();
    String loginUri = String.format("/auth/login?username=%s&password=%s", username, username);
    HttpEntity<String> request = api.httpEntity(null);
    ResponseEntity<ApiResponse> response = delegate.postForEntity(loginUri, request, ApiResponse.class);
    // 쿠키
    Collection<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
    assert cookies != null;
    String sessionIdCookie = cookies.stream().findFirst().orElseThrow();
    // accessToken
    ApiResponse body = response.getBody();
    assert body != null;
    Map<String, String> res = (Map) body.getData();
    assert res != null;
    String accessToken = res.get("compact");
    return new LoginResult(accessToken, sessionIdCookie);
  }

}
