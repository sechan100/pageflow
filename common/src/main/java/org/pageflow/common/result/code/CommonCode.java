package org.pageflow.common.result.code;

import lombok.Getter;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.InvalidField;

/**
 * @author : sechan
 */
@Getter
public enum CommonCode implements ResultCode {
  /**
   * 요청 기본 응답, 실패, 요청 해석, http 관련
   */
    SUCCESS("성공", Object.class)
  , PROCESSING("요청 처리중")
  , FAIL_TO_PARSE_HTTP_REQUEST("http 요청을 해석하지 못했습니다")
  , MISSING_REQUEST_PARAMETER("필수 요청 파라미터 누락")
  , INVALID_COOKIE("유효하지 않은 쿠키", InvalidField.class)

  /**
   * 서버에러
   */
  , INTERNAL_SERVER_ERROR("서버 에러")

  /**
   * 인증
   */
  , LOGIN_REQUIRED("로그인 필요")

  /**
   * 필드 검증
   */
  , FIELD_VALIDATION_FAIL("필드 유효성 검사 실패", FieldValidationResult.class)


  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String message;
  private final Class<?> dataType;

  CommonCode(String message) {
    this(message, null);
  }

  CommonCode(String message, Class<?> dataType) {
    this.message = message;
    this.dataType = dataType;
  }


}
