package org.pageflow.user.port.in;

import org.pageflow.user.dto.UserDto;

/**
 * @author : sechan
 */
public interface SignupUseCase {
  UserDto signup(SignupCmd cmd);
}
