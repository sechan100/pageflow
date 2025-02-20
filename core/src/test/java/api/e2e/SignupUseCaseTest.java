package api.e2e;

import api.API;
import api.ResTestWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.core.PageflowApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@SpringBootTest(classes = PageflowApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
class SignupUseCaseTest {
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