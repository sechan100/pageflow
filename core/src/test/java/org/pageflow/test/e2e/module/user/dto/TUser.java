package org.pageflow.test.e2e.module.user.dto;

import lombok.Builder;
import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
@Builder
public class TUser {
  UID uid;
  String username;
  String password;
  String email;
  boolean isEmailVerified;
  RoleType role;
  String penname;
  String profileImageUrl;
}
