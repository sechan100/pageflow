package org.pageflow.boundedcontext.auth.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.global.api.ApiAccess;
import org.pageflow.global.api.RequestContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * @author : sechan
 */
@Controller
@RequiredArgsConstructor
public class EmailVerificationWebAdapter {
    private final RequestContext requestContext;
    private final EmailVerificationUseCase useCase;


    @Secured(ApiAccess.USER)
    @Operation(summary = "이메일 인증요청 전송", description = "로그인 중인 사용자의 이메일로 인증메일을 전송한다.")
    @PostMapping("/auth/email/send-verification-email")
    @ResponseBody
    public void sendVerificationEmail() {
        useCase.sendVerificationEmail(requestContext.getUid());
    }

    public static final String EMAIL_VERIFICATION_URI = "/auth/email/verify";
    @RequestMapping(EMAIL_VERIFICATION_URI)
    public String verifyEmail(String uid, String authCode) {
        useCase.verify(UID.from(uid), UUID.fromString(authCode));
        return "/user/email-verification-success";
    }
}
