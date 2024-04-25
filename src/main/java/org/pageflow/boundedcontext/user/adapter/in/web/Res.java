package org.pageflow.boundedcontext.user.adapter.in.web;

import lombok.Value;

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
}
