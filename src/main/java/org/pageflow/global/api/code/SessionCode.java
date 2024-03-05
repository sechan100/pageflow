package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum SessionCode implements ApiCode {
      SESSION_EXPIRED("세션이 만료됨; refreshToken 만료")
    , LOGIN_REQUIRED("로그인 필요")
    , INVALID_TOKEN("올바르지 않은 토큰")
    , TOKEN_NOT_FOUND("토큰을 찾을 수 없음")
    , REFRESH_TOKEN_COOKIE_NOT_FOUND("리프레시 토큰 쿠키를 찾을 수 없음")
    ;
    private final String message;
}
