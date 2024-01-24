package org.pageflow.global.exception.business.exception;

import lombok.Getter;
import org.pageflow.global.exception.business.code.ErrorCode;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Set;

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
    
    public BizException.Handler handle() {
        return new Handler(this);
    }
    
    public static class Handler {
        
        private final BizException bizException;
        private Set<ErrorCode> selectedFilterCodes;
        
        public Handler(BizException bizException) {
            this.bizException = bizException;
        }
        
        /**
         * @param code 원하는 에러코드에 해당하는 BizException만을 지정함. 나머지는 그대로 던짐
         */
        public Handler filter(ErrorCode... code) {
            Assert.notEmpty(code, "code must not be empty");
            this.selectedFilterCodes = Set.of(code);
            return this;
        }
        
        /**
         * @param code 새로운 에러코드를 지정하여 해당 코드로 다시 던짐
         */
        public void throwNewCode(ErrorCode code) {
            if(isTarget()) {
                throw new BizException(code);
            }
        }
        
        /**
         * ErrorCode를 넘겨주는 람다식을 작성
         */
        public void process(ErrorCodeProcessor functionalInterface) {
            if(isTarget()) {
                functionalInterface.process(this.bizException.getCode());
            }
        }
        
        @FunctionalInterface
        public interface ErrorCodeProcessor {
            void process(ErrorCode code);
        }
        
        /**
         * selectedFilterCodes가 지정되었다면, 해당 코드들에 속하는지 확인, 아니라면 모든 코드를 철기
         */
        private boolean isTarget() {
            if (selectedFilterCodes == null) {
                return true;
            }
            return selectedFilterCodes.contains(this.bizException.getCode());
        }
    }
}
