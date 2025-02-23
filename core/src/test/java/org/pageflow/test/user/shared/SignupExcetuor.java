package org.pageflow.test.user.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.pageflow.test.shared.API;
import org.pageflow.test.shared.ApiFactory;
import org.pageflow.user.adapter.in.res.UserRes;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class SignupExcetuor {
  private final ApiFactory apiFactory;
  private final ObjectMapper objectMapper;

  public UserRes signup() {
    String username = UUID
      .randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String json = String.format("""
        {
          "username": "%s",
          "password": "%s",
          "email": "%s@pageflow.org",
          "penname": "%s"
        }
      """, username, username, username, username);

    API api = apiFactory.createAPI();
    var result = api.post("/signup", json);
    result.isSuccess();
    UserRes user = objectMapper.convertValue(result.getApiResponse().getData(), UserRes.class);
    return user;
  }
}