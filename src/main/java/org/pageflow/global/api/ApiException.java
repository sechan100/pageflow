package org.pageflow.global.api;

import lombok.Getter;
import org.pageflow.global.api.code.ApiCode;
import org.springframework.lang.Nullable;

@Getter
public class ApiException extends RuntimeException {

    private final ApiCode origin;
    private final ApiResponse<?> apiResponse;

    private ApiException(ApiCode origin, ApiResponse<?> apiResponse){
        super("ApiException: " + origin.getTitle() + "(" + origin.getMessage() + ")");
        this.origin = origin;
        this.apiResponse = apiResponse;
    }

    public <T> ApiException(ApiCode apiCode, @Nullable String feedback, @Nullable T data){
        this(apiCode, ApiResponse.of(apiCode, feedback, data));
    }

}
