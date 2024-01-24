package org.pageflow.global.exception.business.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum SessionCode implements ErrorCode {
      SESSION_EXPIRED("세션이 만료됨; refreshToken 만료")
    , LOGIN_REQUIRED("로그인 필요")
    , INVALID_TOKEN("토큰 정보가 올바르지 않음");
    private final String message;
}
