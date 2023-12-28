package org.pageflow.base.exception;

import org.pageflow.base.exception.code.CommonErrorCode;
import org.pageflow.base.response.ApiStatus;
import org.pageflow.base.response.ResponseDto;
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
    
    // MethodArgumentNotValidException 발생시 -> @Valid로 필드의 유효성 검사에 실패했을 때 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("필드 값이 유효하지 않습니다.");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage) -> existingErrorMessage + " " + newErrorMessage);
                });
        return new ResponseDto(
                ApiStatus.ERROR, // status
                CommonErrorCode.FIELD_VALIDATION_FAIL, // errorCode
                CommonErrorCode.FIELD_VALIDATION_FAIL.getMessageTemplate(), // message
                errors // data
        );
    }
    
    
    // BadRequestException 발생시 -> 바로 사용자에게 피드백을 줘야하는 예외
    @ExceptionHandler(BadRequestException.class)
    public ResponseDto handleBadRequestException(BadRequestException e) {
        return ResponseDto.error(e);
    }
    
 
}
