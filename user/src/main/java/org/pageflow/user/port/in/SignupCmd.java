package org.pageflow.user.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.property.PropsAware;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.Password;

@Getter
@RequiredArgsConstructor
public class SignupCmd {
  private final String username;
  private final Password password;
  private final String email;
  private final String penname;
  private final RoleType role;
  private final ProviderType provider;
  private final String profileImageUrl;


  /**
   * 회원가입시에 profileImageUrl이 지정될 수 있는 것은 OAUTH2 가입자뿐이다.
   */
  public static SignupCmd oAuthSignup(
    String username,
    String password,
    String email,
    String penname,
    RoleType role,
    ProviderType provider,
    String profileImageUrl
  ){
    if(provider == ProviderType.NATIVE){
      throw new ProcessResultException(UserCode.EXTERNAL_PROFILE_IMAGE_URL);
    }
    return new SignupCmd(username, Password.encrypt(password), email, penname, role, provider, profileImageUrl);
  }

  public static SignupCmd nativeSignup(
    String username,
    String password,
    String email,
    String penname,
    RoleType role,
    ProviderType provider
  ){
    if(provider != ProviderType.NATIVE){
      throw new IllegalStateException("Native 회원가입자가 아닙니다.");
    }
    return new SignupCmd(username, Password.encrypt(password), email, penname, role, provider, PropsAware.use().user.defaultProfileImageUrl);
  }
}
