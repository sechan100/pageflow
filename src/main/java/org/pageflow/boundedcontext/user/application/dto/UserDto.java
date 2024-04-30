package org.pageflow.boundedcontext.user.application.dto;

import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class UserDto {

    // user domain의 필트와 동일
    @Value
    public static class User {
        TSID uid;
        String username;
        String email;
        boolean isEmailVerified;
        RoleType role;
        String penname;
        String profileImageUrl;
    }

    @Value
    public static class Session {
        TSID uid;
        String username;
        String email;
        boolean isEmailVerified;
        RoleType role;
        String penname;
        String profileImageUrl;
    }

}
