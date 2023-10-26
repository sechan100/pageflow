package org.pageflow.domain.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DevController {
    
    @GetMapping("/application")
    public String applicationPage() {
        return "/user/application/application";
    }
}
