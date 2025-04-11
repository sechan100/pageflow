package org.pageflow.user.adapter.in.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.pageflow.common.api.UriPrefix;
import org.pageflow.common.utility.Forward;
import org.pageflow.user.dto.UserDto;

/**
 * SpringSecurity를 통한 로그인에 성공한 후, AccessToken과 RefreshToken을 반환하는 endpoint로 forward 시켜주는 객체
 *
 * @author : sechan
 */
public class LoginTokenEndpointForward extends Forward {
  public static final String LOGIN_TOKEN_URI = UriPrefix.PRIVATE + "/auth/login/tokens";
  public static final String USER_URI_ATTR_KEY = "LoginTokenEndpointForward.authedUser";

  private LoginTokenEndpointForward(String forwordUri) {
    super(forwordUri);
  }

  public static LoginTokenEndpointForward of(UserDto user) {
    var forward = new LoginTokenEndpointForward(LOGIN_TOKEN_URI);
    forward.requestAttr(USER_URI_ATTR_KEY, user);
    return forward;
  }

  /**
   * Forward된 request에서 Account 객체를 가져온다.
   *
   * @param request
   * @return
   */
  public static UserDto getForwardedAccount(HttpServletRequest request) {
    var user = request.getAttribute(USER_URI_ATTR_KEY);
    if(user instanceof UserDto) {
      return (UserDto) user;
    } else {
      throw new IllegalArgumentException("Forward된 request에서 User를 가져오는데 실패했습니다.");
    }
  }

}
