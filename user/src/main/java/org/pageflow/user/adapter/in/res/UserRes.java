package org.pageflow.user.adapter.in.res;

import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.IdentifiableUser;
import org.pageflow.user.dto.UserDto;

/**
 * @author : sechan
 */
@Value
public class UserRes implements IdentifiableUser {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  RoleType role;
  String penname;
  String profileImageUrl;


  public static UserRes from(UserDto dto){
    return new UserRes(
      dto.getUid(),
      dto.getUsername(),
      dto.getEmail(),
      dto.isEmailVerified(),
      dto.getRole(),
      dto.getPenname(),
      dto.getProfileImageUrl()
    );
  }
}
