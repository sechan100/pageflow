package org.pageflow.base.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.pageflow.base.exception.UserFeedbackException;
import org.pageflow.base.exception.code.ApiStatusCode;

/**
 * @author : sechan
 */
@Data
@AllArgsConstructor
public class ResponseDto {
    
    private ApiStatus status;
    private ApiStatusCode apiStatusCode;
    private String message;
    private Object data;
    
    
    public static ResponseDto success(Object data){
        return new ResponseDto(ApiStatus.SUCCESS, null, ApiStatus.SUCCESS.getMessage(), data);
    }
    
    public static ResponseDto feedback(UserFeedbackException e){
        return new ResponseDto(ApiStatus.FEEDBACK, (ApiStatusCode) e.getFeedbackCode(), e.getMessage(), null);
    }
    
}
