package org.pageflow.test.e2e.module.user.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.pageflow.test.e2e.module.user.dto.TUser;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
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

  public TUser signup() {
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

    API api = apiFactory.guest();
    var result = api.post("/signup", json);
    result.isSuccess();
    UserRes user = objectMapper.convertValue(result.getApiResponse().getData(), UserRes.class);
    return TUser.builder()
      .uid(user.getUid())
      .username(username)
      .password(username)
      .email(user.getEmail())
      .isEmailVerified(user.isEmailVerified())
      .role(user.getRole())
      .penname(user.getPenname())
      .profileImageUrl(user.getProfileImageUrl())
      .build();
  }
}