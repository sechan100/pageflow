package org.pageflow.domain.support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController {

    @GetMapping("/support")
    public String index() {
        return "/user/support/support";
    }
}
