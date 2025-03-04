package org.pageflow.test.e2e.shared;

import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
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
  private final HttpHeaders headers;

  public API(TestRestTemplate delegate) {
    this.delegate = delegate;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    this.headers = headers;
  }


  public ApiResponseWrapper get(String url) {
    return _wrap(
      delegate.exchange(
        url,
        HttpMethod.GET,
        _httpEntity(null),
        ApiResponse.class
      ).getBody()
    );
  }

  public ApiResponseWrapper post(String url, String jsonBody) {
    return _wrap(
      delegate.exchange(
        url,
        HttpMethod.POST,
        _httpEntity(jsonBody),
        ApiResponse.class
      ).getBody()
    );
  }

  public ApiResponseWrapper delete(String url, String jsonBody) {
    return _wrap(delegate.exchange(
      url,
      HttpMethod.DELETE,
      _httpEntity(jsonBody),
      ApiResponse.class
    ).getBody());
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

  private ApiResponseWrapper _wrap(ApiResponse response) {
    return new ApiResponseWrapper(response);
  }

  private HttpEntity<String> _httpEntity(@Nullable String jsonBody) {
    return new HttpEntity<>(jsonBody, headers);
  }
}
