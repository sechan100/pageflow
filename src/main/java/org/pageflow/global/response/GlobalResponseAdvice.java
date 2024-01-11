package org.pageflow.global.response;

import lombok.RequiredArgsConstructor;
import org.pageflow.global.request.RequestContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "org.pageflow")
@RequiredArgsConstructor
public class GlobalResponseAdvice implements ResponseBodyAdvice {
    
    private final RequestContext requestContext;
    
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        // ResponseDto를 반환하지 않는 메소드에 적용
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
        return GeneralResponse.response(ApiStatus.SUCCESS, body);
    }
    
}