package org.pageflow.boundedcontext.user.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.Email;
import org.pageflow.boundedcontext.user.domain.Password;
import org.pageflow.boundedcontext.user.domain.Penname;
import org.pageflow.boundedcontext.user.domain.ProfileImage;
import org.pageflow.boundedcontext.user.domain.Username;
import org.pageflow.boundedcontext.user.shared.ProviderType;

@Value
public class SignupCmd {
    Username username;
    Password password;
    Email email;
    Penname penname;
    RoleType role;
    ProviderType provider;
    ProfileImage profileImage;
}
