package org.pageflow.base.exception;

import lombok.Getter;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class DomainException extends RuntimeException {
    
    @Nullable
    private DomainError.ErrorCode superErrorCode;
    private final DomainError.ErrorCode subErrorCode;
    private final Object data;
    
    
    public DomainException(DomainError.ErrorCode domainError, Object data) {
        super(domainError.getMessage());
        this.subErrorCode = domainError;
        this.data = data;
    }
    
    public DomainException(DomainError.ErrorCode domainError) {
        super(domainError.getMessage());
        this.subErrorCode = domainError;
        this.data = null;
    }
    
    public DomainException(DomainError.ErrorCode superError, DomainError.ErrorCode subError, Object data) {
        super(superError.getMessage() + "(" + subError.getMessage() + ")");
        this.superErrorCode = superError;
        this.subErrorCode = subError;
        this.data = null;
    }
    
    public DomainException(DomainError.ErrorCode superError, DomainError.ErrorCode subError) {
        super(superError.getMessage() + "(" + subError.getMessage() + ")");
        this.superErrorCode = superError;
        this.subErrorCode = subError;
        this.data = null;
    }
}
