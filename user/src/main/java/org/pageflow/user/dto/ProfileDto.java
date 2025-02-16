package org.pageflow.user.dto;

import lombok.Value;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Profile;

/**
 * @author : sechan
 */
@Value
public class ProfileDto implements IdentifiableUser {
  UID uid;
  String penname;
  String profileImageUrl;

  public static ProfileDto from(Profile profile) {
    return new ProfileDto(
      profile.getUid(),
      profile.getPenname(),
      profile.getProfileImageUrl()
    );
  }
}
