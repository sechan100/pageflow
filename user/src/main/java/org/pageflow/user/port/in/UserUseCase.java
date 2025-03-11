package org.pageflow.user.port.in;

import org.pageflow.common.user.UID;
import org.pageflow.user.dto.UserDto;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface UserUseCase {
  Optional<UserDto> queryUser(UID uid);
}
