package org.pageflow.boundedcontext.user.port.in;

import lombok.Getter;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.shared.ProviderType;

@Getter
public class SignupCmd {
  private final Username username;
  private final Password password;
  private final Email email;
  private final Penname penname;
  private final RoleType role;
  private final ProviderType provider;
  private final ProfileImageUrl profileImageUrl;

  public SignupCmd(
    String username,
    String password,
    String email,
    String penname,
    RoleType role,
    ProviderType provider,
    String profileImageUrl
  ) {
    this.username = Username.from(username);
    this.password = Password.encrypt(password);
    this.email = Email.from(email);
    this.penname = Penname.from(penname);
    this.role = role;
    this.provider = provider;
    this.profileImageUrl = ProfileImageUrl.from(profileImageUrl);
  }
}
