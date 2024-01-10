package org.pageflow.base.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum CommonApiStatusCode implements ErrorCode {
      DATA_NOT_FOUND("요청받은 데이터를 찾을 수 없습니다.")
    , INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다. 잠시후에 다시 시도해주세요.")
    , FIELD_VALIDATION_FAIL("필드 값이 유효하지 않습니다.")
    ;
    private final String message;
}
