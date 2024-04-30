package org.pageflow.boundedcontext.user.port.in;

import org.pageflow.boundedcontext.user.application.dto.UserDto;

/**
 * @author : sechan
 */
public interface AdminUseCase {
    UserDto.Default registerAdmin(SignupCmd cmd);
}
