package org.pageflow.test.module.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.test.e2e.API;
import org.pageflow.test.e2e.PageflowIntegrationTest;
import org.pageflow.test.e2e.TestRes;
import org.pageflow.test.shared.TestResourcePermissionContext;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class SignupTest {
  private final TestRestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final TestResourcePermissionContext permissionContext;

  @Test
  @DisplayName("회원가입")
  void signup() {
    String user1 = """
      {
        "username": "user1",
        "password": "user1",
        "email": "user1@gmail.com",
        "penname": "user1"
      }
    """;

    API e2e = new API(restTemplate, objectMapper, permissionContext);
    // 회원가입
    TestRes result = e2e.post("/signup", user1);
    result.isSuccess();

    // 중복 회원가입
    e2e.post("/signup", user1)
      .is(CommonCode.FIELD_VALIDATION_FAIL);
  }
}