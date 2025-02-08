package org.pageflow.boundedcontext.auth.in.adapter.web;

import lombok.Value;
import org.pageflow.boundedcontext.user.application.dto.UserDto;

/**
 * @author : sechan
 */
abstract class Res {

  @Value
  static class AccessToken {
    String compact;
    long exp;
  }

  @Value
  static class SessionInfo {
    UserDto.Session user;
  }


}
