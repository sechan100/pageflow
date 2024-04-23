package org.pageflow.global.advice;

import org.pageflow.global.api.GeneralResponse;
import org.pageflow.global.api.code.Code2;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "org.pageflow")
public class GeneralResponseAdvice implements ResponseBodyAdvice<Object> {
    
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        // 공통응답 객체를 반환하지 않는 메소드에 적용
        return !returnType.getParameterType().equals(GeneralResponse.class);
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
        return GeneralResponse.withoutFeedback(Code2.SUCCESS, body);
    }
    
}