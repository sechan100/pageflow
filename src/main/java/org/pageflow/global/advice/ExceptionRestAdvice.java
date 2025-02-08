package org.pageflow.global.advice;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.common.exception.FieldValidationException;
import org.pageflow.boundedcontext.common.exception.UniqueFieldDuplicatedException;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.result.code.ResultCode2;
import org.pageflow.global.result.code.ResultCode4;
import org.pageflow.global.result.code.ResultCode5;
import org.pageflow.global.result.exception.ApiException;
import org.pageflow.global.validation.InvalidField;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    return new ApiResponse<>(ResultCode5.INTERNAL_SERVER_ERROR, null);
  }


  // @Valid를 통한 Spring Bean Validation의 필드의 유효성 검사에 실패한 경우
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<InvalidField.Errors> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

    InvalidField.Errors errors = new InvalidField.Errors();
    e.getBindingResult().getFieldErrors()
      .forEach(fieldError -> {
        String fieldName = fieldError.getField();
        String rejectedValue = Objects.requireNonNullElse(fieldError.getRejectedValue(), "null").toString();
        String errorMessage = Objects.requireNonNullElse(fieldError.getDefaultMessage(), "올바르지 않은 값입니다.");
        errors.add(new InvalidField(
          fieldName,
          rejectedValue,
          errorMessage
        ));
      });
    return new ApiResponse<>(ResultCode4.FIELD_VALIDATION_FAIL, errors);
  }

  @ExceptionHandler(FieldValidationException.class)
  public ApiResponse<InvalidField.Errors> handleInputValueException(FieldValidationException e) {
    InvalidField.Errors errors = new InvalidField.Errors();
    errors.add(new InvalidField(
      e.getFieldName(),
      e.getValue(),
      e.getMessage()
    ));
    return new ApiResponse<>(ResultCode4.FIELD_VALIDATION_FAIL, errors);
  }

  @ExceptionHandler(UniqueFieldDuplicatedException.class)
  public ApiResponse<InvalidField.Errors> handleUniqueFieldDuplicatedException(UniqueFieldDuplicatedException e) {
    InvalidField.Errors errors = new InvalidField.Errors();
    errors.add(new InvalidField(
      e.getField(),
      e.getDuplicatedValue(),
      e.getMessage()
    ));
    return new ApiResponse<>(ResultCode4.FIELD_VALIDATION_FAIL, errors);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  public ApiResponse<Void> handleHttpMessageConversionException(HttpMessageConversionException e) {
    return new ApiResponse<>(ResultCode2.FAIL_TO_PARSE_HTTP_REQUEST);
  }

}
