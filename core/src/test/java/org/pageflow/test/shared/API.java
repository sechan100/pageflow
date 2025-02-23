package org.pageflow.test.shared;

import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

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

  public ResTestWrapper post(String url, String jsonBody) {
    return wrap(delegate.postForObject(url, httpEntity(jsonBody), ApiResponse.class));
  }

  public ResTestWrapper get(String url) {
    return wrap(delegate.exchange(
      url,
      HttpMethod.GET,
      httpEntity(null),
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

  public API setCookie(String cookieString) {
    headers.add(HttpHeaders.COOKIE, cookieString);
    return this;
  }

  public API clearCookie() {
    headers.remove(HttpHeaders.COOKIE);
    return this;
  }

  public HttpEntity httpEntity(String jsonBody) {
    return new HttpEntity<>(jsonBody, headers);
  }

  private ResTestWrapper wrap(ApiResponse response) {
    return new ResTestWrapper(response);
  }
}
