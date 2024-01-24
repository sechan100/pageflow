package org.pageflow.global.response;

import lombok.Getter;
import org.pageflow.global.exception.business.code.ErrorCode;
import org.pageflow.global.exception.business.code.GeneralCode;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * GeneralResponse
 * @author : sechan
 */
@Getter
public class GeneralResponse<T> {
    
    private final ErrorCode code;
    private final String message;
    private final T data;
    
    public static <T> GeneralResponse success(T data){
        Assert.notNull(data, "data must not be null");
        
        return GeneralResponse.builder()
                .code(GeneralCode.SUCCESS)
                .message(GeneralCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }
    
    public GeneralResponse(ErrorCode code){
        this.code = code;
        this.message = code.getMessage();
        this.data = null;
    }
    
    private GeneralResponse(ErrorCode code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Builder<T> builder(){
        return new Builder<>();
    }
    
    
    public static class Builder<T> {
        private ErrorCode code;
        private String message;
        private T data;
        
        public Builder code(ErrorCode code){
            this.code = code;
            return this;
        }
        
        public Builder message(String message){
            this.message = message;
            return this;
        }
        
        public Builder data(T data){
            this.data = data;
            return this;
        }
        
        public GeneralResponse<T> build(){
            String message = Objects.requireNonNullElse(this.message, code.getMessage());
            return new GeneralResponse<>(code, message, data);
        }
    }
  
}
