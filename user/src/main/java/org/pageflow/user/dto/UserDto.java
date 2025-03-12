package org.pageflow.user.dto;

import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.domain.entity.Profile;

/**
 * @author : sechan
 */
@Value
public class UserDto implements IdentifiableUser {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  RoleType role;
  String penname;
  String profileImageUrl;


  public static UserDto from(Account account) {
    Profile profile = account.getProfile();
    return new UserDto(
      account.getUid(),
      account.getUsername(),
      account.getEmail(),
      account.getIsEmailVerified(),
      account.getRole(),
      profile.getPenname(),
      profile.getProfileImageUrl()
    );
  }
}
