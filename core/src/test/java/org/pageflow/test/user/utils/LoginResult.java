package org.pageflow.test.user.utils;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class LoginResult {
  String accessToken;
  String sessionIdCookie;
}
