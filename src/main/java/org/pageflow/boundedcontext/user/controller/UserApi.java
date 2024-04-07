package org.pageflow.boundedcontext.user.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.AppAuthority;
import org.pageflow.boundedcontext.user.dto.ApiRevealSignupForm;
import org.pageflow.boundedcontext.user.dto.SignupResult;
import org.pageflow.boundedcontext.user.usecase.UserUsecase;
import org.pageflow.global.api.GeneralResponse;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.code.UserCode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserApi {

    private final RequestContext requestContext;
    private final UserUsecase userUsecase;

    @Secured(AppAuthority.ANONYMOUS)
    @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
    @PostMapping("/signup")
    public SignupResult signup(@Valid @RequestBody ApiRevealSignupForm form) {
        return userUsecase.signup(form);
    }

    @Hidden
    @GetMapping("/internal/pre-signup")
    public GeneralResponse<ApiRevealSignupForm> getPresignupData(String username) {
        return GeneralResponse.builder()
                .apiCode(UserCode.OAUTH2_SIGNUP_REQUIRED)
                .data(userUsecase.getOauth2PresignupData(username))
                .build();
    }
}
