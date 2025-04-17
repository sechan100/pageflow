package org.pageflow.core.advice;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.ResultException;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.validation.FieldValidationException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author : sechan
 */
@Slf4j
@RestControllerAdvice
@SuppressWarnings("HardcodedLineSeparator")
public class ExceptionRestAdvice {

  /**
   * ProcessResultException 처리
   */
  @ExceptionHandler(ResultException.class)
  public ApiResponse handleProcessResultException(ResultException e) {
    log.debug("ProcessResultException을 처리했습니다. CODE: {}", e.getResult().getCode());
    return ApiResponse.of(e.getResult());
  }

  /**
   * HTTP 요청 해석 실패
   */
  @ExceptionHandler(HttpMessageConversionException.class)
  public ApiResponse handleHttpMessageConversionException(HttpMessageConversionException e) {
    return ApiResponse.of(Result.unit(CommonCode.FAIL_TO_PARSE_HTTP_REQUEST));
  }

  /**
   * FieldValidationException
   */
  @ExceptionHandler(FieldValidationException.class)
  public ApiResponse handleFieldValidationException(FieldValidationException e) {
    log.debug("FieldValidationException을 처리했습니다. Fields: {}", e.getResult().getInvalidFields());
    return ApiResponse.of(Result.unit(CommonCode.FIELD_VALIDATION_ERROR, e.getResult()));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ApiResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
    return ApiResponse.of(Result.unit(CommonCode.METHOD_NOT_ALLOWED));
  }

  @ExceptionHandler(Throwable.class)
  public ApiResponse handleException(Throwable e) {
    // TODO: 이거 이메일 보내야댐(911)
    log.error("알 수 없는 예외가 발생하여 INTERNAL_SERVER_ERROR 응답을 반환했습니다. 해당 예외를 처리하려면 적절한 ExceptionHandler를 등록하세요.", e);
    return ApiResponse.of(Result.unit(CommonCode.INTERNAL_SERVER_ERROR));
  }


  private String exceptionString(Exception e) {
    return "\n ====================[ EXCEPTION ]====================\n" + e;
  }
}
