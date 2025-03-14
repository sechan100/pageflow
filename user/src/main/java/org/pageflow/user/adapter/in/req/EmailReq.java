package org.pageflow.user.adapter.in.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
public class EmailReq {
  @NotBlank
  @Email
  private String email;
}
