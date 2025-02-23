package org.pageflow.test.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.test.shared.API;
import org.pageflow.test.shared.PageflowIntegrationTest;
import org.pageflow.test.shared.ResTestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.Rollback;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
public class SignupUseCaseTest {
  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @Rollback
  @DisplayName("/signup")
  void signup() {
    String user1 = """
      {
        "username": "user1",
        "password": "user1",
        "email": "user1@gmail.com",
        "penname": "user1"
      }
    """;

    API e2e = new API(restTemplate);
    // 회원가입
    ResTestWrapper result = e2e.post("/signup", user1);
    result.isSuccess();

    // 중복 회원가입
    e2e.post("/signup", user1)
      .is(CommonCode.FIELD_VALIDATION_FAIL);
  }
}