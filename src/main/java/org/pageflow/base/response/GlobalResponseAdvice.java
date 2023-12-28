package org.pageflow.base.response;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@RestControllerAdvice(basePackages = "org.pageflow")
@RequiredArgsConstructor
public class GlobalResponseAdvice implements ResponseBodyAdvice {
    
    private final Rq rq;
    
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        // ResponseDto를 반환하지 않는 메소드에 적용
        return !returnType.getParameterType().equals(ResponseDto.class);
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
    
//        int status = ((ServletServerHttpResponse)response).getServletResponse().getStatus();
        
        // request Attr에 저장된 ApiStatus가 있다면 사용, 없다면 SUCCESS
        ApiStatus apiStatus = Objects.requireNonNullElse(
                rq.getRequestAttr(ApiStatus.ATTRIBUTE_KEY),
                ApiStatus.SUCCESS
        );
        
        return new ResponseDto(apiStatus, null, apiStatus.getMessage(), body);
    }
    
}