package org.pageflow.user.adapter.in.res;

import lombok.Value;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.IdentifiableUser;
import org.pageflow.user.dto.ProfileDto;

/**
 * @author : sechan
 */
@Value
public class ProfileRes implements IdentifiableUser {
  UID uid;
  String penname;
  String profileImageUrl;

  public static ProfileRes from(ProfileDto dto){
    return new ProfileRes(
      dto.getUid(),
      dto.getPenname(),
      dto.getProfileImageUrl()
    );
  }
}
