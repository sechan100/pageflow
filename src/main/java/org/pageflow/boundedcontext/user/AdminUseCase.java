package org.pageflow.boundedcontext.user;

import org.pageflow.boundedcontext.user.application.dto.UserDto;

/**
 * @author : sechan
 */
public interface AdminUseCase {
    UserDto.User registerAdmin(SignupCmd cmd);
}
