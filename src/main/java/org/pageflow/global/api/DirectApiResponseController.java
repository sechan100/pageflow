package org.pageflow.global.api;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.pageflow.global.filter.ApiExceptionCatchAndDelegatingFilter;
import org.pageflow.global.filter.UriPrefix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@AllArgsConstructor
public class DirectApiResponseController {

    public static final String SEND_GR_ANY_WHERE_ENDPOINT = UriPrefix.PRIVATE + "/send-response/apiResponse";
    @RequestMapping(SEND_GR_ANY_WHERE_ENDPOINT)
    @Hidden
    public ApiResponse<?> response(HttpServletRequest request){
        return(ApiResponse<?>) request.getAttribute(ApiExceptionCatchAndDelegatingFilter.API_RESPONSE_REQUEST_ATTR);
    }
}
