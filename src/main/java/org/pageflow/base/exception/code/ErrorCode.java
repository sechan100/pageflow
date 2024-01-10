package org.pageflow.base.exception.code;

/**
 * {@link org.pageflow.base.exception.code.ApiStatusCode } 상태중, ERROR 상태의 세부 상태코드
 * @author : sechan
 */
public interface ErrorCode extends ApiStatusCode {
    String getMessage();
}
