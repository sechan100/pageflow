package org.pageflow.boundedcontext.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.pageflow.boundedcontext.user.dto.AccountDto;
import org.pageflow.boundedcontext.user.dto.ProfileDto;
import org.pageflow.boundedcontext.user.dto.SignupResult;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.pageflow.boundedcontext.user.entity.ProfileEntity;
import org.pageflow.global.config.MapStructConfig;

/**
 * @author : sechan
 */
@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    AccountDto entityToDto(AccountEntity accountEntity);
    ProfileDto entityToDto(ProfileEntity profileEntity);

    @Mapping(target = "id", source = "accountDto.id")
    SignupResult dtoToSignupResult(AccountDto accountDto, ProfileDto profileDto);
}
