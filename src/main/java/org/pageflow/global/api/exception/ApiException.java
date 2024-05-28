package org.pageflow.global.api.exception;

import lombok.Getter;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.api.code.ApiCode;
import org.springframework.lang.Nullable;

@Getter
public class ApiException extends RuntimeException {
    private final ApiResponse apiResponse;

    public ApiException(ApiCode apiCode, @Nullable Object data) {
        super(apiCode.getMessage());
        this.apiResponse = new ApiResponse(apiCode, data);
    }

    public ApiException(ApiCode apiCode) {
        this(apiCode, null);
    }

}
