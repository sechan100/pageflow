package org.pageflow.user.application;

import lombok.Getter;
import org.pageflow.common.result.code.ResultCode;
import org.pageflow.user.adapter.in.res.PreSignupedUser;

import java.util.Collection;

/**
 * @author : sechan
 */
@Getter
public enum UserCode implements ResultCode {
    OAUTH2_SIGNUP_REQUIRED("OAuth2 회원가입이 필요", PreSignupedUser.class)

  // 세션
  , ALREADY_LOGOUT("이미 로그아웃됨")
  , SESSION_EXPIRED("세션(refreshToken) 만료")
  , ACCESS_TOKEN_EXPIRED("AccessToken 만료")

  // 로그인
  , USERNAME_NOT_FOUND("아이디를 찾을 수 없음")
  , BAD_CREDENTIALS("자격증명 실패")

  // 기타
  , EXTERNAL_PROFILE_IMAGE_URL("외부 프로필 이미지 url은 사용할 수 없습니다.")

  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String message;
  private final Class<?> dataType;
  private final Class<? extends Collection> collectionType;

  UserCode(String message) {
    this(message, null, null);
  }

  UserCode(String message, Class<?> dataType) {
    this(message, dataType, null);
  }

  UserCode(String message, Class<?> dataType, Class<? extends Collection> collectionType) {
    this.message = message;
    this.dataType = dataType;
    this.collectionType = collectionType;
  }
}
