package org.pageflow.user.adapter.in.auth.oauth2.presignup;

import jakarta.servlet.http.HttpServletRequest;
import org.pageflow.common.api.UriPrefix;
import org.pageflow.common.shared.utility.Forward;
import org.pageflow.user.adapter.in.auth.oauth2.owner.OAuth2ResourceOwner;

/**
 * @author : sechan
 */
public class OAuth2PreSignupForward extends Forward {
  public static final String OAUTH2_PRE_SIGNUP_PATH = UriPrefix.PRIVATE + "/oauth2/pre-signup";
  public static final String RESOURCE_OWNER_REQUEST_ATTR_KEY = "OAuth2PreSignupForward.resourceOwner";

  private OAuth2PreSignupForward(String forwordUri) {
    super(forwordUri);
  }

  public static OAuth2PreSignupForward of(OAuth2ResourceOwner owner) {
    var forward = new OAuth2PreSignupForward(OAUTH2_PRE_SIGNUP_PATH);
    forward.requestAttr(RESOURCE_OWNER_REQUEST_ATTR_KEY, owner);
    return forward;
  }

  /**
   * Forward된 request에서 OAuth2ResourceOwner 객체를 가져온다.
   * @param request
   * @return
   */
  public static OAuth2ResourceOwner getForwardedResourceOwner(HttpServletRequest request) {
    var owner = request.getAttribute(RESOURCE_OWNER_REQUEST_ATTR_KEY);
    if(owner instanceof OAuth2ResourceOwner) {
      return (OAuth2ResourceOwner) owner;
    } else {
      throw new IllegalArgumentException("Forward된 request에서 OAuth2ResourceOwner 객체를 가져오는데 실패했습니다. owner: " + owner);
    }
  }
}
