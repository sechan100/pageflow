package org.pageflow.global.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.pageflow.global.api.GeneralResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@AllArgsConstructor
public class GrResponseController {

    static final String SEND_GR_ANY_WHERE_ENDPOINT = "/PRIVATE/send-response/gr";
    @RequestMapping(SEND_GR_ANY_WHERE_ENDPOINT)
    public GeneralResponse<?> responseGr(HttpServletRequest request){
        return(GeneralResponse<?>) request.getAttribute(ApiExceptionCatchAndDelegatingFilter.GENERAL_RESPONSE_REQUEST_ATTR);
    }
}
