package org.pageflow.base.exception.entityaccessdenied;

import lombok.NoArgsConstructor;

/**
 * @author : sechan
 */
@NoArgsConstructor
public class WebEntityAccessDeniedException extends RuntimeException {
    
    public WebEntityAccessDeniedException(String message) {
        super(message);
    }

}
