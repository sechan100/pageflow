package org.pageflow.base.exception.data;

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
