package org.pageflow.test.shared;

import lombok.RequiredArgsConstructor;
import org.pageflow.test.user.shared.LoginResult;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class ApiFactory {
  private final TestRestTemplate restTemplate;


  public API createAPI() {
    return new API(restTemplate);
  }

  public API createAuthenticated(LoginResult login) {
    var api = new API(restTemplate);
    api.setAccessToken(login.getAccessToken());
    api.setCookie(login.getSessionIdCookie());
    return api;
  }

}
