package org.pageflow.boundedcontext.user.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
abstract class Req {

    @Data
    public static class SignupForm {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @Email
        private String email;

        @NotBlank
        private String penname;
    }
}
