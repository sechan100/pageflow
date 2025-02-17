package org.pageflow.user.adapter.in.web;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.ApiAccess;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.shared.annotation.SecuredPost;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.user.adapter.in.auth.oauth2.owner.OAuth2ResourceOwner;
import org.pageflow.user.adapter.in.auth.oauth2.presignup.OAuth2PreSignupForward;
import org.pageflow.user.adapter.in.auth.oauth2.presignup.OAuth2PreSignupService;
import org.pageflow.user.adapter.in.auth.oauth2.presignup.PreSignupDto;
import org.pageflow.user.adapter.in.req.SignupForm;
import org.pageflow.user.adapter.in.res.PreSignupedUser;
import org.pageflow.user.adapter.in.res.UserRes;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "user-signup", description = "회원가입(OAuth2, Native)")
public class SignupWebAdapter {
  private final RequestContext requestContext;
  private final OAuth2PreSignupService preSignupService;
  private final SignupUseCase signupUseCase;


  @SecuredPost(value = "/signup", access = ApiAccess.ANONYMOUS)
  @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
  public UserRes signup(@RequestBody SignupForm form) {
    Optional<PreSignupDto> preSignupOptional = preSignupService.loadAndRemove(form.getUsername());

    SignupCmd cmd = null;
    // OAuth
    if(preSignupOptional.isPresent()){
      PreSignupDto preSignup = preSignupOptional.get();
      cmd = SignupCmd.oAuthSignup(
        form.getUsername(),
        UUID.randomUUID().toString(),
        form.getEmail(),
        form.getPenname(),
        RoleType.ROLE_USER,
        preSignup.getProvider(),
        preSignup.getProfileImageUrl()
      );
    // Native
    } else {
      cmd = SignupCmd.nativeSignup(
        form.getUsername(),
        form.getPassword(),
        form.getEmail(),
        form.getPenname(),
        RoleType.ROLE_USER,
        ProviderType.NATIVE
      );
    }

    UserDto userDto = signupUseCase.signup(cmd);
    return UserRes.from(userDto);
  }


  /**
   * OAuth2 회원가입 전처리 로직
   *
   * SpringSecurityFilter에서 OAuth2를 통해서 인증할 때, 아직 회원이 아닌 사용자라면 해당 endpoint로 forward한다.
   * OAuth2 Provider에게 받아온 데이터를 임시로 저장하고 반환한다.
   * 사용자는 이 데이터를 기반으로 다시한번 회원가입을 요청해야한다.
   */
  @SecuredPost(value = OAuth2PreSignupForward.OAUTH2_PRE_SIGNUP_PATH, access = ApiAccess.ANONYMOUS)
  @Hidden
  @Operation(summary = "OAuth2 회원가입 전처리", description = "OAuth2를 통해 받아온 사용자 정보를 회원가입을 위해 서버에 임시 저장")
  public Result<PreSignupedUser> oAuth2PreSignup() {
    OAuth2ResourceOwner owner = OAuth2PreSignupForward.getForwardedResourceOwner(requestContext.getRequest());
    PreSignupDto preSignuped = preSignupService.preSignup(owner);

    return Result.of(UserCode.OAUTH2_SIGNUP_REQUIRED, preSignuped);
  }

}
