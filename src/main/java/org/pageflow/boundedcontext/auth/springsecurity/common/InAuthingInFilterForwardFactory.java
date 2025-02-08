package org.pageflow.boundedcontext.auth.springsecurity.common;

import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.in.adapter.web.AuthWebAdapter;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.OAuth2ResourceOwner;
import org.pageflow.boundedcontext.user.adapter.in.web.UserWebAdapter;
import org.pageflow.shared.utility.Forward;

/**
 * @author : sechan
 */
public abstract class InAuthingInFilterForwardFactory {

  public static Forward getLoginFoward(Account alreadyAuthedAccount) {
    return Forward.confrimInServletRequestContext()
      .path(AuthWebAdapter.LOGIN_URI)
      .requestAttr(AuthWebAdapter.ACCOUNT_REQUEST_ATTR_KEY, alreadyAuthedAccount);
  }

  public static Forward getOAuth2PreSignupForward(OAuth2ResourceOwner resourceOwner) {
    return Forward.confrimInServletRequestContext()
      .path(UserWebAdapter.PRE_SIGNUP_PATH)
      .requestAttr(UserWebAdapter.RESOURCE_OWNER_REQUEST_ATTR_KEY, resourceOwner);
  }
}
