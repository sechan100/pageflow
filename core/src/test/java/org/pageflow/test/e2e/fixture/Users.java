package org.pageflow.test.e2e.fixture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.user.RoleType;
import org.pageflow.test.e2e.shared.fixture.TestFixture;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.springframework.stereotype.Component;



/**
 * @author : sechan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Users implements TestFixture {
  private static final int USER_COUNT = 10;
  private final SignupUseCase signupUseCase;

  @Override
  public void configure() {
    for(int i = 0; i < USER_COUNT; i++){
      UserDto signup = signupUseCase.signup(_getCmd("user" + (i + 1)));
    }
    log.info("=== Users Fixture 적용됨 ===");
  }

  private SignupCmd _getCmd(String username){
    return SignupCmd.nativeSignup(
      username,
      username,
      username + "@pageflow.org",
      username,
      RoleType.ROLE_USER
    );
  }
}
