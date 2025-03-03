package org.pageflow.test.e2e.user.shared;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class LoginResult {
  String accessToken;
  String sessionIdCookie;
}
