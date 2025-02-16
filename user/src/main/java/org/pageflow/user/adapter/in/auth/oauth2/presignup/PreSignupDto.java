package org.pageflow.user.adapter.in.auth.oauth2.presignup;

import lombok.Value;
import org.pageflow.common.user.ProviderType;

/**
 * @author : sechan
 */
@Value
public class PreSignupDto {
  String username;
  ProviderType provider;
  String profileImageUrl;

  public static PreSignupDto from(OAuth2PreSignup preSignup) {
    return new PreSignupDto(
      preSignup.getId(),
      preSignup.getData().getProvider(),
      preSignup.getData().getProfileImageUrl()
    );
  }
}
