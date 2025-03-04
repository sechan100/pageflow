package org.pageflow.test.e2e.data.fixture;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.user.RoleType;
import org.pageflow.test.e2e.data.DataFixture;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.springframework.stereotype.Component;



/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class Signup implements DataFixture {
  private static final int USER_COUNT = 10;
  private final SignupUseCase signupUseCase;

  @Override
  public void configure() {
    for(int i = 0; i < USER_COUNT; i++){
      UserDto signup = signupUseCase.signup(_getCmd("user" + (i + 1)));
      System.out.println("사용자 데이터 생성 완료" + signup.getUsername());
    }
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
