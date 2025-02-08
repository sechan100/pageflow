package org.pageflow.boundedcontext.user.adapter.in.web;

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

    @jakarta.validation.constraints.Email
    private String email;

    @NotBlank
    private String penname;
  }

  @Data
  public static class Penname {
    @NotBlank
    private String penname;
  }

  @Data
  public static class Email {
    @NotBlank
    private String email;
  }
}
