package org.pageflow.test.e2e;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class LoginResult {
  String accessToken;
  String sessionIdCookie;
}
