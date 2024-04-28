package org.pageflow.boundedcontext.user.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.global.config.MapStructConfig;

/**
 * @author : sechan
 */
@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    @Mapping(target = "username", source = "cmd.username.value")
    @Mapping(target = "email", source = "cmd.email.value")
    @Mapping(target = "penname", source = "cmd.penname.value")
    @Mapping(target = "isEmailVerified", expression = "java(cmd.getEmail().isVerified())")
    @Mapping(target = "profileImageUrl", source = "cmd.profileImage.value")
    @Mapping(target = "uid", expression = "java(uid.getValue())")
    UserDto.Signup signupDto_UidAndsignupCmd(UID uid, SignupCmd cmd);
}
