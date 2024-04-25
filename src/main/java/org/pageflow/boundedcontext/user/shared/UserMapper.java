package org.pageflow.boundedcontext.user.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.AccountJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.ProfileJpaEntity;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
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

    default User user_jpaEntities(AccountJpaEntity a, ProfileJpaEntity p){
        return new User(
            UID.from(a.getId()),
            Username.of(a.getUsername()),
            Email.of(a.getEmail(), a.isEmailVerified()),
            Penname.of(p.getPenname()),
            ProfileImage.of(p.getProfileImageUrl())
        );
    }
}
