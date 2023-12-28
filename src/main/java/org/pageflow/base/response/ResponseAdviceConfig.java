package org.pageflow.base.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "org.pageflow")
public class ResponseAdviceConfig implements ResponseBodyAdvice {
    
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        // ResponseDto를 반환하지 않는 메소드에 적용
        return !returnType.getParameterType().equals(ResponseDto.class);
    }
    
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
//        // body가 String이면 감쌌을 때 예외가 발생하므로, 그대로 반환함
//        if(body instanceof String){
//            return body;
//        }
        int status = ((ServletServerHttpResponse)response).getServletResponse().getStatus();
        return ResponseDto.success(body);
    }
    
}