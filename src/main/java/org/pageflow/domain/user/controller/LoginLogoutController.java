package org.pageflow.domain.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class LoginLogoutController {
    
    
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @GetMapping("/login")
    public String loginWithRequestBody(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        return "login";
    }
    
    
    // OAuth2 인가 요청 리디렉션 매핑
    @Hidden
    @GetMapping("/internal/login")
    public String loginWithQueryString(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return "login";
    }

}
