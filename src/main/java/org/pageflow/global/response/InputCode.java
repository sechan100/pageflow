package org.pageflow.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum InputCode implements ErrorCode {
    FIELD_VALIDATION("사용자 입력 필드 검증 오류"),
    
    ;
    private final String message;
}
