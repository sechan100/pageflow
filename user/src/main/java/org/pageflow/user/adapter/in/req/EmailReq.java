package org.pageflow.user.adapter.in.req;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailReq {
  @Email
  private String email;
}