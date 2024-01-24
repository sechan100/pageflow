package org.pageflow.global.response;

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
    ;
    private final String message;
}
