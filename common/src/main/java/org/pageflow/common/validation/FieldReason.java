package org.pageflow.common.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public enum FieldReason {
  // 일반
    INVALID_VALUE("입력값이 올바르지 않습니다.")
  , NULL("입력값이 없습니다.")
  , EMPTY("입력값이 없습니다.")
  , INVALID_FORMAT("입력값이 올바르지 않은 형식입니다.")
  , CONTAINS_INVALID_VALUE("입력값에 올바르지 않은 값이 포함되어 있습니다.")

  // 문자열
  , TOO_LONG("입력값이 너무 깁니다.")
  , TOO_SHORT("입력값이 너무 짧습니다.")

  // 수
  , TOO_SMALL("입력값이 너무 작습니다.")
  , TOO_LARGE("입력값이 너무 큽니다.")
  , OUT_OF_RANGE("입력값이 범위를 벗어났습니다.")

  // 기타
  , DUPLICATED("중복된 값입니다.")
  ;

  private final String defaultMessage;
}
