package org.pageflow.user.adapter.in.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
public class ChangePasswordReq {
  @NotBlank
  private String currentPassword;
  @NotBlank
  private String newPassword;
}
