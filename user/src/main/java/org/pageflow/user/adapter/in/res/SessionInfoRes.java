package org.pageflow.user.adapter.in.res;

import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.IdentifiableUser;
import org.pageflow.user.dto.SessionUserDto;

/**
 * @author : sechan
 */
@Value
public class SessionInfoRes {
  SessionUser user;

  @Value
  public static class SessionUser implements IdentifiableUser {
    UID uid;
    String username;
    String email;
    boolean isEmailVerified;
    RoleType role;
    String penname;
    String profileImageUrl;

    public static SessionUser from(SessionUserDto dto){
      return new SessionUser(
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
}
