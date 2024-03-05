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
    ;
    private final String message;
}
