package org.pageflow.global.result.code;

import lombok.Getter;

import java.util.Collection;

/**
 * <p>LEVEL: 1000</p>
 * <p>
 * 사용자의 인증, 또는 인가와 관련된 에러를 다룬다.
 * </p>
 *
 * @author : sechan
 */
@Getter
public enum ResultCode1 implements ResultCode {
  // 1000: LOGIN
  LOGIN_REQUIRED(1000, "로그인 필요")

  // 1100: LOGOUT

  // 1200: SESSION
  ,
  SESSION_EXPIRED(1200, "세션(refreshToken) 만료")


  // 1410: ACCESS_TOKEN
  ,
  ACCESS_TOKEN_EXPIRED(1410, "AccessToken 만료")

  // 1700: 이메일 인증
  ,
  EMAIL_VERIFICATION_AUTH_CODE_MISMATCH(1700, "이메일 인증 코드 불일치"),
  APPLIED_EMAIL_VERIFICATION_NOT_FOUND(1701, "요청된 이메일 인증 정보가 없음"),
  ALRADY_VERIFIED_EMAIL(1702, "이미 인증된 이메일")


  ////////////////////////////////////////////////////////////////////////////
  ;
  private final int code;
  private final String message;
  private final Class<?> dataType;
  private final Class<? extends Collection> collectionType;

  ResultCode1(int code, String message) {
    this(code, message, null, null);
  }

  ResultCode1(int code, String message, Class<?> dataType) {
    this(code, message, dataType, null);
  }

  ResultCode1(int code, String message, Class<?> dataType, Class<? extends Collection> collectionType) {
    this.code = code;
    this.message = message;
    this.dataType = dataType;
    this.collectionType = collectionType;
  }

}
