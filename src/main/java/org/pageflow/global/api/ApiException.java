package org.pageflow.global.api;

import lombok.Getter;
import org.pageflow.global.api.code.ApiCode;
import org.springframework.lang.Nullable;

public class ApiException extends RuntimeException {

    @Getter
    private final GeneralResponse<?> gr;

    private ApiException(GeneralResponse<?> gr){
        super("Api Response: " + gr.getTitle() + "(" + gr.getMessage() + ")");
        this.gr = gr;
    }

    public <T> ApiException(ApiCode apiCode, @Nullable String feedback, @Nullable T data){
        this(GeneralResponse.of(apiCode, feedback, data));
    }

}
