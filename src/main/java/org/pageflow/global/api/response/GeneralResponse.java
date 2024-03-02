package org.pageflow.global.api.response;

import lombok.Getter;
import org.pageflow.global.api.code.ApiCode;
import org.pageflow.global.api.code.GeneralCode;

import java.util.Objects;

/**
 * GeneralResponse
 * @author : sechan
 */
@Getter
public class GeneralResponse<T> {
    
    private final ApiCode apiCode;
    private final String message;
    private final T data;
    
    public static <T> GeneralResponse success(T data){
        return GeneralResponse.builder()
                .apiCode(GeneralCode.SUCCESS)
                .message(GeneralCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }
    
    public GeneralResponse(ApiCode apiCode){
        this.apiCode = apiCode;
        this.message = apiCode.getMessage();
        this.data = null;
    }
    
    private GeneralResponse(ApiCode apiCode, String message, T data){
        this.apiCode = apiCode;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Builder<T> builder(){
        return new Builder<>();
    }
    
    
    public static class Builder<T> {
        private ApiCode apiCode;
        private String message;
        private T data;
        
        public Builder apiCode(ApiCode apiCode){
            this.apiCode = apiCode;
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
            String message = Objects.requireNonNullElse(this.message, apiCode.getMessage());
            return new GeneralResponse<>(apiCode, message, data);
        }
    }
  
}
