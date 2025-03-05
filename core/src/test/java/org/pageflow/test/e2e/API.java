package org.pageflow.test.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.test.shared.TestResourcePermissionContext;
import org.pageflow.user.adapter.in.res.SessionInfoRes;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class API {
  private final TestRestTemplate delegate;
  private final ObjectMapper objectMapper;
  private final TestResourcePermissionContext permissionContext;
  private final HttpHeaders headers;

  public API(
    TestRestTemplate delegate,
    ObjectMapper objectMapper,
    TestResourcePermissionContext permissionContext
  ) {
    this.delegate = delegate;
    this.objectMapper = objectMapper;
    this.permissionContext = permissionContext;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    this.headers = headers;
  }


  public TestRes get(String url) {
    return _wrap(
      delegate.exchange(
        url,
        HttpMethod.GET,
        _httpEntity(null),
        String.class
      ).getBody());
  }

  public TestRes post(String url, String jsonBody) {
    return _wrap(
      delegate.exchange(
        url,
        HttpMethod.POST,
        _httpEntity(jsonBody),
        String.class
      ).getBody());
  }

  public TestRes delete(String url, String jsonBody) {
    return _wrap(delegate.exchange(
      url,
      HttpMethod.DELETE,
      _httpEntity(jsonBody),
      String.class
    ).getBody());
  }

  public TestRes delete(String url) {
    return _wrap(delegate.exchange(
      url,
      HttpMethod.DELETE,
      _httpEntity(null),
      String.class
    ).getBody());
  }


  // 예약 api
  public SessionInfoRes.SessionUser getSessionUser() {
    JsonNode data = this.get("/user/session").getData();
    return objectMapper.convertValue(data, SessionInfoRes.class).getUser();
  }


  public API setAccessToken(String accessToken) {
    headers.setBearerAuth(accessToken);
    return this;
  }

  public API clearAccessToken() {
    headers.remove(HttpHeaders.AUTHORIZATION);
    return this;
  }

  /**
   * 아예 문자열을 통째로 바꾸는 방식으로 쿠키를 설정한다.
   * 만약 기존 쿠키를 유지하면서 쿠키를 추가하고싶다면 다른 방식을 사용해야한다.
   * @param cookieString
   * @return
   */
  public API setSessionIdCookie(String cookieString) {
    headers.add(HttpHeaders.COOKIE, cookieString);
    return this;
  }

  /**
   * 없으면 null을 반환한다.
   * @return
   */
  public String getSessionIdCookie() {
    return headers.getFirst(HttpHeaders.COOKIE);
  }

  public API clearSessionIdCookie() {
    headers.remove(HttpHeaders.COOKIE);
    return this;
  }

  private TestRes _wrap(String response) {
    try {
      JsonNode data = objectMapper.readTree(response).get("data");
      return new TestRes(objectMapper.readValue(response, ApiResponse.class), data);
    } catch(JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      permissionContext.clear();
    }
  }

  private HttpEntity<String> _httpEntity(@Nullable String jsonBody) {
    return new HttpEntity<>(jsonBody, headers);
  }
}
