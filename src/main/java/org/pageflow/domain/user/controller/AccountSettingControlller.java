package org.pageflow.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AccountSettingControlller {

    private final Rq rq;

    @GetMapping("/account/settings")
    public String accountSettings(Model model) {

        model.addAttribute("account", rq.getAccount());

        return "/user/account/settings";
    }

}
