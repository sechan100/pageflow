package org.pageflow.boundedcontext.auth.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.global.api.RequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class EmailVerificationWebAdapter {
    private final RequestContext requestContext;
    private final EmailVerificationUseCase useCase;


    public static final String EMAIL_VERIFICATION_URI = "/user/email/send-verification-email";
    @Operation(summary = "이메일 인증요청 전송", description = "로그인 중인 사용자의 이메일로 인증메일을 전송한다.")
    @PostMapping(EMAIL_VERIFICATION_URI)
    public void sendVerificationEmail() {
        useCase.sendVerificationEmail(requestContext.getUid());
    }
}
