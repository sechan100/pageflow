package org.pageflow.base.exception;

import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class DomainException extends RuntimeException {
    
    private DomainError.ErrorCode errorCode;
    
    public DomainException(DomainError.ErrorCode domainError) {
        super(domainError.getMessage());
        this.errorCode = domainError;
    }
    
    public DomainException(DomainError.ErrorCode domainError, String msg) {
        super(msg);
        this.errorCode = domainError;
    }
    
    
}
