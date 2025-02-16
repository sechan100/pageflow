package org.pageflow.user.adapter.in.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PennameReq {
  @NotBlank
  private String penname;
}