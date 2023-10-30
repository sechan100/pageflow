package org.pageflow.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountSettingControlller {
    
    @GetMapping("/account/settings")
    public String accountSettings() {
        return "/user/account/settings";
    }
}
