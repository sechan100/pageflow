package org.pageflow.user.dto;

import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.User;

/**
 * @author : sechan
 */
@Value
public class UserDto {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  RoleType role;
  String penname;
  String profileImageUrl;

  public UserDto(User user) {
    this.uid = user.getUid();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.isEmailVerified = user.getIsEmailVerified();
    this.role = user.getRole();
    this.penname = user.getPenname();
    this.profileImageUrl = user.getProfileImageUrl();
  }
}
