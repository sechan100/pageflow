package org.pageflow.boundedcontext.auth.domain.exception;

import org.pageflow.boundedcontext.common.exception.DomainException;

/**
 * @author : sechan
 */
public class AccessTokenExpiredException extends DomainException {

    public AccessTokenExpiredException(Throwable cause) {
        super(cause);
    }
}
