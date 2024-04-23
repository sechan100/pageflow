package org.pageflow.boundedcontext.user.adapter.in.web;

import lombok.Builder;
import lombok.Data;

/**
 * @author : sechan
 */
public abstract class UserRes {

    @Data
    @Builder
    public static class Signup {
        private String username;
        private String email;
        private String penname;
    }

    @Data
    @Builder
    public static class PreSignuped {
        private String username;
        private String email;
        private String penname;
    }
}
