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
  SUCCESS("성공", Object.class),
  PROCESSING("요청 처리중"),
  FAIL_TO_PARSE_HTTP_REQUEST("http 요청을 해석하지 못했습니다"),
  MISSING_REQUEST_PARAMETER("필수 요청 파라미터 누락"),
  INVALID_COOKIE("유효하지 않은 쿠키", InvalidField.class),
  METHOD_NOT_ALLOWED("허용되지 않은 메소드"),

  /**
   * 서버에러
   */
  INTERNAL_SERVER_ERROR("서버 에러"),

  /**
   * 인증
   */
  LOGIN_REQUIRED("로그인 필요"),

  /**
   * 필드 검증
   */
  FIELD_VALIDATION_ERROR("필드 유효성 검사 실패", FieldValidationResult.class),

  /**
   * 데이터 접근
   */
  RESOURCE_PERMISSION_DENIED("리소스 접근 권한 없음"),
  DATA_NOT_FOUND("데이터를 찾을 수 없음", String.class),


  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String description;
  private final Class<?> dataType;

  CommonCode(String description) {
    this(description, null);
  }

  CommonCode(String description, Class<?> dataType) {
    this.description = description;
    this.dataType = dataType;
  }


}
