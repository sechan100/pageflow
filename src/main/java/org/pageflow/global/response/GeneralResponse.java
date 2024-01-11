package org.pageflow.global.response;

import lombok.Getter;
import org.springframework.util.Assert;

/**
 * GeneralResponse
 * @author : sechan
 */
@Getter
public class GeneralResponse<T> {
    
    private final ApiStatus status;
    private final String message;
    private final T data;
    
    private GeneralResponse(ApiStatus status, T data){
        this.status = status;
        this.message = status.getMessage();
        this.data = data;
    }
    
    /**
     * @param status - 응답 상태코드
     * @param data - 상태코드에서 허용하는 타입의 데이터
     * @return 응답 객체
     * @throws InavailableDataTypeException - status에서 허용하지 않는 타입의 data를 전달한 경우
     */
    public static <T> GeneralResponse<T> response(ApiStatus status, T data){
        Assert.notNull(status, "status must not be null");
        
        // data가 필요한 status
        if(status.getAvailableDataType() != null){
            // data가 null인지 확인
            if(data == null) {
                throw new InavailableDataTypeException(status + "응답 상태는 null 데이터를 허용하지 않습니다. " +
                        "허용 가능한 데이터 타입:" + status.getAvailableDataType().getName());
            }
            
            // data가 허용되는 타입인지 확인
            if(!status.getAvailableDataType().isAssignableFrom(data.getClass())){
                throw new InavailableDataTypeException("허용되지 않는 데이터 타입입니다. " +
                        "허용된 데이터 타입: " + status.getAvailableDataType().getName());
            }
        }
        
        // data를 받지 않는 status
        if(status.getAvailableDataType() == null){
            if(data != null){
                throw new InavailableDataTypeException(status + "응답 상태는 데이터를 허용하지 않습니다.");
            }
        }
        
        return new GeneralResponse(status, data);
    }
    
}
