package org.pageflow.global.api.code;

import org.pageflow.global.api.ApiResponse;
import org.springframework.lang.Nullable;

/**
 * ApiCode의 구현체인 enum을 통해서만 생성
 */
public class ApiException extends RuntimeException {

    private final ApiCode origin;
    private final ApiResponse<?> apiResponse;

    <T> ApiException(ApiCode origin, @Nullable String feedback, @Nullable T data, @Nullable Throwable cause){
        super("ApiException: " + origin.getTitle() + "(" + origin.getMessage() + ")", cause);
        this.origin = origin;
        this.apiResponse = ApiResponse.of(origin, feedback, data);
    }

    public ApiCode getOriginApiCode() {
        return origin;
    }

    public ApiResponse<?> getApiResponse() {
        return apiResponse;
    }
}
