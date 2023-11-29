package org.pageflow.base.exception.entityaccessdenied;

import lombok.NoArgsConstructor;

/**
 * @author : sechan
 */
@NoArgsConstructor
public class ApiEntityAccessDeniedException extends RuntimeException {
    
    public ApiEntityAccessDeniedException(String message) {
        super(message);
    }

}
