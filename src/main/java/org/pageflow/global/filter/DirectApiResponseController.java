package org.pageflow.global.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.pageflow.global.api.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@AllArgsConstructor
public class DirectApiResponseController {

    static final String SEND_GR_ANY_WHERE_ENDPOINT = UriPrefix.PRIVATE + "/send-response/apiResponse";
    @RequestMapping(SEND_GR_ANY_WHERE_ENDPOINT)
    public ApiResponse<?> response(HttpServletRequest request){
        return(ApiResponse<?>) request.getAttribute(ApiExceptionCatchAndDelegatingFilter.Api_RESPONSE_REQUEST_ATTR);
    }
}
