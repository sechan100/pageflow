package org.pageflow.global.advice;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.api.ResDataTypes;
import org.pageflow.global.api.code.ApiCode2;
import org.pageflow.global.api.code.ApiCode4;
import org.pageflow.global.api.code.ApiCode5;
import org.pageflow.global.api.exception.ApiException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : sechan
 */
@Slf4j
@RestControllerAdvice
@SuppressWarnings("HardcodedLineSeparator")
public class ExceptionRestAdvice {

    @ExceptionHandler(ApiException.class)
    public ApiResponse<?> handleApiException(ApiException e) {
        log.trace("ApiException을 advice에서 처리 \n ====================[ EXCEPTION ]====================", e);
        return e.getApiResponse();
    }

    @ExceptionHandler(Throwable.class)
    public ApiResponse<?> handleException(Throwable e) {
        log.debug("Throwable을 advice에서 INTERNAL_SERVER_ERROR로 처리 \n ====================[ EXCEPTION ]====================", e);
        return new ApiResponse<>(ApiCode5.INTERNAL_SERVER_ERROR, null);
    }

    
    // @Valid를 통한 Spring Bean Validation의 필드의 유효성 검사에 실패한 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<ResDataTypes.FieldValidation> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        List<ResDataTypes.FieldError> errors = new ArrayList<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String rejectedValue = Objects.requireNonNullElse(fieldError.getRejectedValue(), "null").toString();
                    String errorMessage = Objects.requireNonNullElse(fieldError.getDefaultMessage(), "올바르지 않은 값입니다.");
                    errors.add(new ResDataTypes.FieldError(
                        fieldName,
                        rejectedValue,
                        errorMessage
                    ));
                });
        return new ApiResponse<>(ApiCode4.FIELD_VALIDATION_FAIL, new ResDataTypes.FieldValidation(errors));
    }

    @ExceptionHandler(InputValueException.class)
    public ApiResponse<ResDataTypes.FieldValidation> handleInputValueException(InputValueException e) {
        List<ResDataTypes.FieldError> errors = new ArrayList<>();
        errors.add(new ResDataTypes.FieldError(
            e.getFieldName(),
            e.getValue(),
            e.getMessage()
        ));
        return new ApiResponse<>(ApiCode4.FIELD_VALIDATION_FAIL, new ResDataTypes.FieldValidation(errors));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ApiResponse<Void> handleHttpMessageConversionException(HttpMessageConversionException e) {
        return new ApiResponse<>(ApiCode2.FAIL_TO_PARSE_HTTP_REQUEST);
    }
 
}
