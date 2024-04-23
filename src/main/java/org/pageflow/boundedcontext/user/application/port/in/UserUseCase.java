package org.pageflow.boundedcontext.user.application.port.in;

import org.pageflow.boundedcontext.user.application.dto.UserDto;

/**
 * @author : sechan
 */
public interface UserUseCase {
    UserDto.Signup signup(SignupCmd cmd);
}
