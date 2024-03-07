package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum GeneralCode implements ApiCode {
      SUCCESS("성공")
    , REQUIRE_COOKIE("쿠키가 필요함")
    , DATA_ACCESS_ERROR("데이터 접근 오류")
    ;
    private final String message;
}
