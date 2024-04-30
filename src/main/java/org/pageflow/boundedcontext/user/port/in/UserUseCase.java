package org.pageflow.boundedcontext.user.port.in;

import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.Penname;

/**
 * @author : sechan
 */
public interface UserUseCase {
    UserDto.Default signup(SignupCmd cmd);
    UserDto.Default changeEmail(UID uid, Email email);
    UserDto.Default changePenname(UID uid, Penname penname);
    UserDto.Default changeProfileImage(UID uid, ProfileImageFile file);
}
