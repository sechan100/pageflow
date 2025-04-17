package org.pageflow.test.module.user.utils;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.user.RoleType;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Component
@Transactional
@RequiredArgsConstructor
public class UserUtils {
  private final SignupUseCase signupUseCase;

  public UserDto createUser(String username) {
    SignupCmd cmd = SignupCmd.nativeSignup(
      username,
      username,
      username + "@pageflow.org",
      username,
      RoleType.ROLE_USER
    ).getSuccessData();
    return signupUseCase.signup(cmd);
  }
}
