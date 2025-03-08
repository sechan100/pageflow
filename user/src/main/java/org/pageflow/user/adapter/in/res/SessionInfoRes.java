package org.pageflow.user.adapter.in.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.IdentifiableUser;
import org.pageflow.user.dto.SessionUserDto;

/**
 * @author : sechan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfoRes {
  private SessionUser user;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SessionUser implements IdentifiableUser {
    private UID uid;
    private String username;
    private String email;
    private boolean isEmailVerified;
    private RoleType role;
    private String penname;
    private String profileImageUrl;

    public static SessionUser from(SessionUserDto dto){
      return new SessionUser(
        dto.getUid(),
        dto.getUsername(),
        dto.getEmail(),
        dto.getIsEmailVerified(),
        dto.getRole(),
        dto.getPenname(),
        dto.getProfileImageUrl()
      );
    }
  }
}
