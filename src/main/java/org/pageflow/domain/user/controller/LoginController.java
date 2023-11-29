package org.pageflow.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final Rq rq;

    /**
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        
        // 로그인 상태라면 메인 페이지로 리다이렉트
        if(rq.getUserSession().isLogin()) {
            return "redirect:/";
        }
        
        return "/user/account/login";
    }

}