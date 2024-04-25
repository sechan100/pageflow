package org.pageflow.global.api.code;

import org.pageflow.global.api.ApiResponse;
import org.springframework.lang.Nullable;

/**
 * ApiCode의 구현체인 enum을 통해서만 생성
 */
public class ApiException extends RuntimeException {

    private final ApiCode origin;
    private final ApiResponse<?> apiResponse;

    private ApiException(ApiCode origin, ApiResponse<?> apiResponse){
        super("ApiException: " + origin.getTitle() + "(" + origin.getMessage() + ")");
        this.origin = origin;
        this.apiResponse = apiResponse;
    }

    <T> ApiException(ApiCode apiCode, @Nullable String feedback, @Nullable T data){
        this(apiCode, ApiResponse.of(apiCode, feedback, data));
    }

    public ApiCode getOriginApiCode() {
        return origin;
    }

    public ApiResponse<?> getApiResponse() {
        return apiResponse;
    }
}
