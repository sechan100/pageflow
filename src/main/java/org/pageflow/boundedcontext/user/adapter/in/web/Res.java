package org.pageflow.boundedcontext.user.adapter.in.web;

import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
abstract class Res {

    @Value
    public static class Signup {
        private String username;
        private String email;
        private String penname;
    }

    @Value
    public static class PreSignuped {
        private String username;
        private String email;
        private String penname;
    }

    @Value
    public static class SessionUser {
        TSID uid;
        String username;
        String email;
        boolean isEmailVerified;
        RoleType role;
        String penname;
        String profileImageUrl;
    }
}
