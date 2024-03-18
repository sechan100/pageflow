package org.pageflow.global.api.code;

import org.pageflow.global.api.BizException;

/**
 * @author : sechan
 */
public interface ApiCode {
    String getMessage();
    
    default BizException fire() {
        return new BizException(this);
    }
    
    default BizException fire(String message) {
        return BizException.builder().code(this).message(message).build();
    }
    
    default BizException fireWithData(Object data) {
        return BizException.builder()
                .code(this)
                .data(data)
                .build();
    }
    
    default void predicate(boolean condition) {
        if(condition) {
            throw fire();
        }
    }
    
    default void predicate(boolean condition, String message) {
        if(condition) {
            throw fire(message);
        }
    }
}
