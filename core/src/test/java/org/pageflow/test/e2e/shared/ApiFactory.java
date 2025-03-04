package org.pageflow.test.e2e.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
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
public class ApiFactory {
  private final TestRestTemplate restTemplate;
  private final AccountPersistencePort port;
  private final ObjectMapper objectMapper;


  public API guest() {
    return new API(restTemplate, objectMapper);
  }

  public API user(String username, String password) {
    LoginResult login = _login(username, password);
    API api = new API(restTemplate, objectMapper);
    api.setAccessToken(login.getAccessToken());
    api.setSessionIdCookie(login.getSessionIdCookie());
    return api;
  }



  private LoginResult _login(String username, String password){
    API guestApi = this.guest();

    // 로그인 요청 전송
    TestRestTemplate delegate = guestApi.getDelegate();
    String loginUri = String.format("/auth/login?username=%s&password=%s", username, password);
    HttpEntity<String> request = new HttpEntity<>(null, guestApi.getHeaders());
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
