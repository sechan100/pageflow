package org.pageflow.global.business;

import lombok.Getter;

import java.text.MessageFormat;


/**
 * 비지니스 논리상 어긋나는 상황에 대한 예외 클래스
 */
public class BizException extends RuntimeException {
    
    @Getter
    private final BizConstraint bizConstraint;
    private final String message;
    
    public BizException(BizConstraint domainError, Object... args) {
        super();
        this.bizConstraint = domainError;
        
        MessageFormat messageFormat = new MessageFormat(domainError.getMessageTemplate());
        this.message = messageFormat.format(args);
    }
    
    
    @Override
    public String getMessage() {
        return message;
    }
    
}
