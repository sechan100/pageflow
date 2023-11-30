package org.pageflow.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Transactional
public class PasswordFindController {

    @GetMapping("/find")
    public String passwordFind(){
        return "/user/account/password_Find";
    }
}
