package org.pageflow.global.exception.business.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum InputCode implements ApiCode {
    FIELD_VALIDATION("사용자 입력 필드 검증 오류"),
    
    ;
    private final String message;
}
