package org.pageflow.global.api.exception;

/**
 * @author : sechan
 */
public class ApiResponseDataTypeMisMatchException extends RuntimeException {
    private final Class<?> expectedType;
    private final Class<?> actualType;

    public ApiResponseDataTypeMisMatchException(Class<?> expected, Class<?> actual) {
        super("ApiResponse가 정의된 data 타입을 반환하지 않았습니다. Expected type: " + expected + ", Actual type: " + actual);
        this.expectedType = expected;
        this.actualType = actual;
    }
}
