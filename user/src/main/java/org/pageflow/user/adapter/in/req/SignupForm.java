package org.pageflow.user.adapter.in.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupForm {
  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @Email
  private String email;

  @NotBlank
  private String penname;
}