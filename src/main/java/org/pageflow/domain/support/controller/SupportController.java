package org.pageflow.domain.support.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController {

    @GetMapping("/support")
    public String supportMain() {
        
        return "/user/support/support";
    }
}
