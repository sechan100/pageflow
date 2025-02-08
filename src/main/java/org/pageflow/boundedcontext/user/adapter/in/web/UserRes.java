package org.pageflow.boundedcontext.user.adapter.in.web;

import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;

import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class UserRes {

  @Value
  public static class Signup {
    String username;
    String email;
    String penname;
  }

  @Value
  public static class PreSignuped {
    String username;
    String email;
    String penname;
  }

  @Value
  public static class SessionUser {
    UUID uid;
    String username;
    String email;
    boolean isEmailVerified;
    RoleType role;
    String penname;
    String profileImageUrl;
  }
}
