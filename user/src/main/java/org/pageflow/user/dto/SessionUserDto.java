package org.pageflow.user.dto;

import lombok.Builder;
import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
@Builder
public class SessionUserDto implements IdentifiableUser {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  RoleType role;
  String penname;
  String profileImageUrl;
}
