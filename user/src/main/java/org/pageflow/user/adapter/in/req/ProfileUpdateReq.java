package org.pageflow.user.adapter.in.req;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ProfileUpdateReq {
  @Nullable
  private String penname;
  @Nullable
  private String email;
}