package org.pageflow.test.user.shared;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class LoginResult {
  String accessToken;
  String sessionIdCookie;
}
