package org.pageflow.domain.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.jwt.JwtProvider;
import org.pageflow.domain.user.jwt.TokenDto;
import org.pageflow.domain.user.model.dto.WebLoginRequest;
import org.pageflow.domain.user.service.DefaultUserService;
import org.springframework.web.bind.annotation.*;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class LoginLogoutController {
    
    private final DefaultUserService defaultUserService;
    private final JwtProvider jwtProvider;
    
    
    
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받고, access 토큰과 refresh 토큰을 반환")
    @PostMapping("/login")
    public TokenDto loginWithRequestBody(@Valid @RequestBody WebLoginRequest login) {
        
        return defaultUserService.login(login.getUsername(), login.getPassword());
    }
    
    
    // OAuth2 인가 요청 리디렉션 매핑
    @Hidden
    @GetMapping("/internal/login")
    public TokenDto oauth2InternalLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return defaultUserService.login(username, password);
    }

}
