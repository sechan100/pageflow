package org.pageflow.boundedcontext.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    /**
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "/user/account/login";
    }
    
}