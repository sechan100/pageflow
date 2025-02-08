package org.pageflow.boundedcontext.auth.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.domain.Email;

/**
 * @author : sechan
 */
@Getter
public class Account {
  private UID uid;
  private String username;
  private EncryptedPassword password;
  private Email email;
  private boolean isEmailVerified;
  private RoleType role;

  public boolean passwordMatches(String rawPassword) {
    return password.matches(rawPassword);
  }

  public void verifyEmail() {
    this.isEmailVerified = true;
  }

  public void unverifyEmail() {
    this.isEmailVerified = false;
  }



  public Account(
    UID uid,
    String username,
    EncryptedPassword password,
    Email email,
    boolean isEmailVerified,
    RoleType role
  ) {
    this.uid = uid;
    this.username = username;
    this.password = password;
    this.email = email;
    this.isEmailVerified = isEmailVerified;
    this.role = role;
  }
}
