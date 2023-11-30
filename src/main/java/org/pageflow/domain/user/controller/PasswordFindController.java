package org.pageflow.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Transactional
public class PasswordFindController {

    // 1차 회원 정보 입력
    @GetMapping("/find")
    public String passwordFind(){
        return "/user/account/password_Find";
    }


    // 2차 회원 정보 입력 후 비밀번호 재 설정
    @GetMapping("/reset")
    public String passwordReset(){
        return "/user/account/password_Reset";
    }
}
