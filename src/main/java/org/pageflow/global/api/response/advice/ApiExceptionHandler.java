package org.pageflow.global.api.response.advice;

import org.pageflow.global.api.response.GeneralResponse;
import org.pageflow.global.api.code.InputCode;
import org.pageflow.global.api.code.exception.BizException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

/**
 * @author : sechan
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    
    // @Valid를 통한 Spring Bean Validation의 필드의 유효성 검사에 실패한 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GeneralResponse<Map<String, String[]>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        
        Map<String, List<String>> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Objects.requireNonNullElse(fieldError.getDefaultMessage(), "올바르지 않은 값입니다.");
                    
                    // 이미 해당 필드가 에러로 등록되어있는 경우
                    if(errors.containsKey(fieldName)){
                        errors.get(fieldName).add(errorMessage);
                    }
                    // 해당 필드가 처음으로 에러로 등록되는 경우
                    else {
                        errors.put(fieldName, new ArrayList<>());
                        errors.get(fieldName).add(errorMessage);
                    }
                });
        
        // 필드별 에러 메세지를 배열로 변환
        Map<String, String[]> result = new LinkedHashMap<>();
        errors.forEach((fieldName, errorMessageList) -> {
            String[] errorMessageArray = errorMessageList.toArray(new String[0]);
            result.put(fieldName, errorMessageArray);
        });
        
        return GeneralResponse.builder()
                .apiCode(InputCode.FIELD_VALIDATION)
                .data(result)
                .build();
    }
    
    
    // BizException에 대한 일반적 처리
    @ExceptionHandler(BizException.class)
    public GeneralResponse<Object> handleBizException(BizException e) {
        return GeneralResponse.builder()
                .apiCode(e.getApiCode())
                .message(e.getMessage())
                .data(e.getData())
                .build();
    }
 
}
