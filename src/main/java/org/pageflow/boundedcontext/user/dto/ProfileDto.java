package org.pageflow.boundedcontext.user.dto;

import lombok.Builder;
import lombok.Data;
import org.pageflow.boundedcontext.user.entity.ProfileEntity;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
@Data
@Builder
public class ProfileDto {
    private final TSID id;
    private final String penname;
    private final String profileImgUrl;

    public static ProfileDto from(ProfileEntity profile){
        return ProfileDto.builder()
            .id(profile.getId())
            .penname(profile.getPenname())
            .profileImgUrl(profile.getProfileImgUrl())
            .build();
    }
}
