package org.pageflow.global.advice;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.api.ApiException;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.api.code.Code4;
import org.pageflow.global.api.code.Code5;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

/**
 * @author : sechan
 */
@Slf4j
@RestControllerAdvice
@SuppressWarnings("HardcodedLineSeparator")
public class ExceptionRestAdvice {

    @ExceptionHandler(ApiException.class)
    public ApiResponse<?> handleApiException(ApiException e) {
        return e.getApiResponse();
    }

    @ExceptionHandler(Throwable.class)
    public ApiResponse<Void> handleException(Throwable e) {
        log.debug("Throwable을 advice에서 INTERNAL_SERVER_ERROR로 처리 \n ====================[ EXCEPTION ]====================", e);
        return ApiResponse.withoutFeedback(Code5.INTERNAL_SERVER_ERROR, null);
    }

    
    // @Valid를 통한 Spring Bean Validation의 필드의 유효성 검사에 실패한 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String[]>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        
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
        
        return ApiResponse.withoutFeedback(Code4.FIELD_VALIDATION_FAIL, result);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<Void> handleNoSuchElementException(NoSuchElementException e) {
        log.debug("NoSuchElementException의 경우 Spring Date 스펙에서 사용하는 Optional에서 발생했을 가능성이 농후하니, Optional에서 발생하는지 먼저 확인할 것");
        // 어디서 발생한지 모르는 범용적인 예외이니, 상세한 정보를 제공하지 않는다.
        return ApiResponse.withoutFeedback(Code5.INTERNAL_SERVER_ERROR, null);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ApiResponse<Void> handleHttpMessageConversionException(HttpMessageConversionException e) {
        return ApiResponse.withoutFeedback(Code4.FIELD_PARSE_FAIL, null);
    }
 
}
