package org.pageflow.user.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.property.PropsAware;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
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


  private static Result<SignupCmd> _of(
    String username,
    String rawPassword,
    String email,
    String penname,
    RoleType role,
    ProviderType provider,
    String profileImageUrl
  ) {
    // password 처리
    Result<Password> encryptedPasswordResult = Password.encrypt(rawPassword);
    if(encryptedPasswordResult.isFailure()) {
      return (Result) encryptedPasswordResult;
    }
    Password encryptedPassword = encryptedPasswordResult.get();

    // signup cmd 생성
    SignupCmd cmd = new SignupCmd(username, encryptedPassword, email, penname, role, provider, profileImageUrl);
    return Result.SUCCESS(cmd);
  }


  /**
   * 회원가입시에 profileImageUrl이 지정될 수 있는 것은 OAUTH2 가입자뿐이다.
   *
   * @return Result
   * - FIELD_VALIDATION_ERROR : 비밀번호가 유효하지 않을 때
   * - EXTERNAL_PROFILE_IMAGE_URL : OAUTH2 가입자는 profileImageUrl을 지정할 수 없다.
   * @throws IllegalArgumentException: ProviderType이 NATIVE인 경우
   */
  public static Result<SignupCmd> oAuthSignup(
    String username,
    String rawPassword,
    String email,
    String penname,
    RoleType role,
    ProviderType provider,
    String profileImageUrl
  ) {
    if(provider == ProviderType.NATIVE) {
      throw new IllegalArgumentException("OAUTH2 가입자의 ProvierType이 NATIVE입니다. 정말 NATIVE 가입자라면 다른 함수를 사용해주세요.");
    }

    return _of(
      username,
      rawPassword,
      email,
      penname,
      role,
      provider,
      profileImageUrl
    );
  }

  public static Result<SignupCmd> nativeSignup(
    String username,
    String rawPassword,
    String email,
    String penname,
    RoleType role
  ) {
    return _of(
      username,
      rawPassword,
      email,
      penname,
      role,
      ProviderType.NATIVE,
      PropsAware.use().user.defaultProfileImageUrl
    );
  }
}
