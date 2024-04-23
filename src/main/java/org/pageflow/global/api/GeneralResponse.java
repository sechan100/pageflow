package org.pageflow.global.api;

import lombok.Getter;
import org.pageflow.global.api.code.ApiCode;
import org.springframework.lang.Nullable;

/**
 * GeneralResponse
 * @author : sechan
 */
@Getter
public class GeneralResponse<T> {

    private final String title;
    private final int code;
    private final String message;
    private final String detail;
    @Nullable
    private final T data;


    private GeneralResponse(ApiCode apiCode, String feedback, @Nullable T data){
        this.title = apiCode.getTitle();
        this.code = apiCode.getCode();
        this.message = apiCode.getMessage();
        this.detail = feedback;
        this.data = data;
    }

    public static <T> GeneralResponse<T> of(ApiCode apiCode, String feedback, @Nullable T data){
        return new GeneralResponse<>(apiCode, feedback, data);
    }

    public static <T> GeneralResponse<T> withoutFeedback(ApiCode apiCode, @Nullable T data){
        return new GeneralResponse<>(apiCode, apiCode.getFeedback(), data);
    }

}
