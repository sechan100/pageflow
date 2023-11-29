package org.pageflow.base.exception.data;

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
