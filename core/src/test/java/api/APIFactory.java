package api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;
import user.auth.LoginResult;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class APIFactory {
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
