package org.pageflow.boundedcontext.user.application.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.domain.*;
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
