package org.pageflow.global.exception.handler;

import org.pageflow.global.business.BizException;
import org.pageflow.global.response.ApiStatus;
import org.pageflow.global.response.InavailableDataTypeException;
import org.pageflow.global.response.GeneralResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author : sechan
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    
    // @Valid를 통한 Spring Bean Validation의 필드의 유효성 검사에 실패한 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GeneralResponse<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("필드 값이 유효하지 않습니다.");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage) -> existingErrorMessage + " " + newErrorMessage);
                });
        
        return GeneralResponse.response(ApiStatus.FIELD_VALIDATION_ERROR, errors);
    }
    
    
    // 비지니스 요구사항을 위반하는 예외 발생시
    @ExceptionHandler(BizException.class)
    public GeneralResponse handleBadRequestException(BizException e) {
        // 기본적으로 적절하게 처리되지 않은 BizException은 ApiStatus.FEEDBACK으로 처리함
        return GeneralResponse.response(ApiStatus.FEEDBACK, e.getBizConstraint());
    }
    
    // ApiStatus에 허용되지 않는 데이터 타입으로 응답을 보내어 처리할 수 없는 경우
    @ExceptionHandler(InavailableDataTypeException.class)
    public GeneralResponse handleInavailableDataTypeException(InavailableDataTypeException e) {
        return GeneralResponse.response(ApiStatus.INAVAILABLE_DATA_TYPE, e.getMessage());
    }
    
 
}
