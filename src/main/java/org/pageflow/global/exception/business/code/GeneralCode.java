package org.pageflow.global.exception.business.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum GeneralCode implements ErrorCode {
      SUCCESS("성공")
    
    ;
    private final String message;
}
