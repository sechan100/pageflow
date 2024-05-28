package org.pageflow.global.api;

import lombok.Getter;
import org.pageflow.global.api.code.ApiCode;
import org.pageflow.global.api.exception.ApiResponseDataTypeMisMatchException;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author : sechan
 */
@Getter
public class ApiResponse<T> {

    private final String title;
    private final int code;
    private final String message;
    @Nullable
    private final T data;


    public ApiResponse(ApiCode apiCode, @Nullable T data){
        Class<?> expectedDataType = Objects.requireNonNullElse(apiCode.getDataType(), NullDataType.class);
        Class<?> actualDataType = Objects.requireNonNullElse(data, new NullDataType()).getClass();
        if(!expectedDataType.isAssignableFrom(actualDataType)){
            throw new ApiResponseDataTypeMisMatchException(expectedDataType, actualDataType);
        }
        this.title = apiCode.getTitle();
        this.code = apiCode.getCode();
        this.message = apiCode.getMessage();
        this.data = data;
    }

    public ApiResponse(ApiCode apiCode){
        this(apiCode, null);
    }

}

// ApiResponse의 dataType이 null인 경우를 표현하기 위한 타입 객체
class NullDataType {
}
