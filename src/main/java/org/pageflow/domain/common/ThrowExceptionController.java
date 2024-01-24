package org.pageflow.domain.common;

import lombok.AllArgsConstructor;
import org.pageflow.global.request.RequestContext;
import org.pageflow.global.response.BizException;
import org.pageflow.global.response.GeneralResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Security와 같이 MVC 디스패처 위에서 동작하지 않는 객체들에서 발생한 예외들을
 * 일괄적으로 처리할 수 있도록 매핑해주는 컨트롤러
 * @author : sechan
 */
@RestController
@AllArgsConstructor
public class ThrowExceptionController {
    
    private final RequestContext requestContext;
    
    @RequestMapping("/internal/throw/biz")
     public GeneralResponse<Object> throwBizException() {
        BizException e = requestContext.getRequestAttr(BizException.class.getSimpleName());
         return GeneralResponse.builder()
                 .code(e.getCode())
                 .message(e.getMessage())
                 .data(e.getData())
                 .build();
     }

}
