package org.pageflow.user.adapter.in.res;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class AccessTokenRes {
  String compact;
  long exp;
}
