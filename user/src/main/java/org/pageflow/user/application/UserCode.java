package org.pageflow.user.application;

import lombok.Getter;
import org.pageflow.common.result.code.ResultCode;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.user.adapter.in.res.PreSignupedUser;

/**
 * @author : sechan
 */
@Getter
public enum UserCode implements ResultCode {
  OAUTH2_SIGNUP_REQUIRED("OAuth2 회원가입이 필요", PreSignupedUser.class),

  // 세션
  SESSION_EXPIRED("세션(refreshToken) 만료"),
  ACCESS_TOKEN_EXPIRED("AccessToken 만료"),

  // 로그인
  BAD_CREDENTIALS("자격증명 실패"),

  // 이메일 인증
  EMAIL_VERIFICATION_ERROR("이메일 인증에 실패했습니다."),
  INVALID_EMAIL("유효하지 않은 이메일입니다.", FieldValidationResult.class),
  EMAIL_VERIFICATION_EXPIRED("이메일 인증 요청이 만료되었거나 없습니다."),

  // 기타
  EXTERNAL_PROFILE_IMAGE_URL("외부 프로필 이미지 url은 사용할 수 없습니다."),
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  PASSWORD_SAME_AS_BEFORE("이전 비밀번호와 동일합니다."),

  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String description;
  private final Class<?> dataType;

  UserCode(String description) {
    this(description, null);
  }

  UserCode(String description, Class<?> dataType) {
    this.description = description;
    this.dataType = dataType;
  }
}
