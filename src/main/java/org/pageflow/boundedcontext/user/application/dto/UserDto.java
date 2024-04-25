package org.pageflow.boundedcontext.user.application.dto;

import lombok.Builder;
import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class UserDto {

    @Value
    @Builder
    public static class Signup {
        TSID uid;
        String username;
        String email;
        boolean isEmailVerified;
        ProviderType provider;
        RoleType role;
        String penname;
        String profileImageUrl;
    }

    @Value
    @Builder
    public static class Session {
        TSID uid;
        String username;
        String email;
        boolean isEmailVerified;
        String penname;
        String profileImageUrl;
    }

}
