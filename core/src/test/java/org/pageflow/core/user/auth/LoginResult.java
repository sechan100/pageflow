package org.pageflow.core.user.auth;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class LoginResult {
  String accessToken;
  String sessionIdCookie;
}
