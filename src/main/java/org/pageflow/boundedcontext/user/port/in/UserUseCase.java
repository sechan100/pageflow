package org.pageflow.boundedcontext.user.port.in;

import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.Penname;

/**
 * @author : sechan
 */
public interface UserUseCase {
    UserDto.User signup(SignupCmd cmd);
    UserDto.User changeEmail(UID uid, Email email);
    UserDto.User changePenname(UID uid, Penname penname);
    UserDto.User changeProfileImage(UID uid, ProfileImageFile file);
}
