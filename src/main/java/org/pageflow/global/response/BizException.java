package org.pageflow.global.response;

import lombok.Getter;

import java.util.Objects;

@Getter
public class BizException extends RuntimeException {
    
    private final ErrorCode code;
    private final String message;
    private final Object data;
    
    public BizException(ErrorCode code) {
        this(code, code.getMessage(), null);
    }
    
    private BizException(ErrorCode code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    
    public static BizException.Builder builder(){
        return new BizException.Builder();
    }
    
    
    public static class Builder {
        private ErrorCode code;
        private String message;
        private Object data;
        
        public BizException.Builder code(ErrorCode code){
            this.code = code;
            return this;
        }
        
        public BizException.Builder message(String message){
            this.message = message;
            return this;
        }
        
        public BizException.Builder data(Object data){
            this.data = data;
            return this;
        }
        
        public BizException build(){
            String message = Objects.requireNonNullElse(this.message, code.getMessage());
            return new BizException(code, message, data);
        }
    }
}
