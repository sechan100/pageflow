package org.pageflow.base.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.pageflow.base.exception.UserFeedbackException;
import org.pageflow.base.exception.code.ErrorCode;

/**
 * @author : sechan
 */
@Data
@AllArgsConstructor
public class ResponseDto {
    
    private ApiStatus status;
    private ErrorCode errorCode;
    private String message;
    private Object data;
    
    
    public static ResponseDto success(Object data){
        return new ResponseDto(ApiStatus.SUCCESS, null, ApiStatus.SUCCESS.getMessage(), data);
    }
    
    public static ResponseDto error(UserFeedbackException e){
        return new ResponseDto(ApiStatus.ERROR, e.getErrorCode(), e.getMessage(), null);
    }
    
}
