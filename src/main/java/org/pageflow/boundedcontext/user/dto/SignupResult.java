package org.pageflow.boundedcontext.user.dto;

import lombok.Data;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.shared.type.TSID;

@Data
public class SignupResult {
    private final TSID id;
    private final String username;
    private final String email;
    private final boolean emailVerified;
    private final ProviderType provider;
    private final RoleType role;
    private final String penname;
    private final String profileImgUrl;
}