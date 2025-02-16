package org.pageflow.core.advice;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 애플리케이션의 모든 controller에서 반환하는 데이터를 공통 응답객체인 {@link ApiResponse}로 래핑한다.
 *
 * ApiResponse는 애플리케이션에서 더 일반적으로(http 응답이 아닌 함수등의 결과로서) 사용되는 {@link Result}로부터
 * 생성되는데, 만약 Controller에서 Result나 ApiResponse 자체를 반환한 경우는 이를 반영하여 응답을 구성한다.
 */
@Slf4j
@RestControllerAdvice(basePackages = "org.pageflow")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

  /**
   * {@link ApiResponse}를 반환하지 않은 대부분의 Controller에 대해서 해당 Advice를 적용한다.
   * @param returnType
   * @param converterType
   * @return
   */
  @Override
  public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
    return !returnType.getParameterType().equals(ApiResponse.class);
  }

  @Override
  public Object beforeBodyWrite(
    Object body,
    @NonNull MethodParameter returnType,
    @NonNull MediaType selectedContentType,
    @NonNull Class selectedConverterType,
    @NonNull ServerHttpRequest request,
    @NonNull ServerHttpResponse response
  ) {
    Result result = body instanceof Result ? (Result) body : Result.of(CommonCode.SUCCESS, body);
    return ApiResponse.of(result);
  }

}