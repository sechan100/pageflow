package org.pageflow.user.adapter.in.res;

import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.UserDto;

/**
 * @author : sechan
 */
@Value
public class UserRes {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  RoleType role;
  String penname;
  String profileImageUrl;

  public UserRes(UserDto dto) {
    this.uid = dto.getUid();
    this.username = dto.getUsername();
    this.email = dto.getEmail();
    this.isEmailVerified = dto.getIsEmailVerified();
    this.role = dto.getRole();
    this.penname = dto.getPenname();
    this.profileImageUrl = dto.getProfileImageUrl();
  }
}
