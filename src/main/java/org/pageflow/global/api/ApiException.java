package org.pageflow.global.api;

import lombok.Getter;
import org.pageflow.global.api.code.ApiCode;
import org.springframework.lang.Nullable;

@Getter
public class ApiException extends RuntimeException {

    private final ApiCode origin;
    private final GeneralResponse<?> gr;

    private ApiException(ApiCode origin, GeneralResponse<?> gr){
        super("ApiException: " + origin.getTitle() + "(" + origin.getMessage() + ")");
        this.origin = origin;
        this.gr = gr;
    }

    public <T> ApiException(ApiCode apiCode, @Nullable String feedback, @Nullable T data){
        this(apiCode, GeneralResponse.of(apiCode, feedback, data));
    }

}
