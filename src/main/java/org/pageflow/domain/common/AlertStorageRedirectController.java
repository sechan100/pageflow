package org.pageflow.domain.common;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.AlertType;
import org.pageflow.base.request.Rq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AlertStorageRedirectController {

    private final Rq rq;

    /**
     * /common/alertStorage 페이지를 렌더링해서 바로 응답할 수 없는 경우에 사용한다.
     * 이쪽으로 리다이렉트하고, 이 메소드의 응답 페이지에서 다시한번 이동.. 총 3번에 걸쳐 이동한다.
     */
    @GetMapping("/common/alertStorage")
    public String alertStorageRedirect(@RequestParam("alertType") String alertTypeString, @RequestParam String msg, @RequestParam(required = false) String redirectUri) {
        AlertType alertType = switch (alertTypeString) {
            case "success" -> AlertType.SUCCESS;
            case "warning" -> AlertType.WARNING;
            case "neutral" -> AlertType.NEUTRAL;
            case "error" -> AlertType.ERROR;
            default -> AlertType.INFO; // info OR other
        };

        if (redirectUri != null) {
            return rq.alert(alertType, msg, redirectUri);
        } else {
            return rq.alert(alertType, msg);
        }
    }
}
