package org.pageflow.boundedcontext.auth.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.shared.annotation.WebAdapter;

/**
 * @author : sechan
 */
@WebAdapter
@RequiredArgsConstructor
public class EmailVerificationWebAdapter {
    private final EmailVerificationUseCase useCase;


    public static final String EMAIL_VERIFICATION_URI = "/email/verify";
    public void verifyEmail(String email) {
        emailVerificationService.verifyEmail(email);
    }
}
