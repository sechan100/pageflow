package org.pageflow.boundedcontext.auth.port.in;

import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.common.value.Email;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface EmailVerificationUseCase {
    void sendVerificationEmail(Email email);
    void verify(EmailVerification.Id evId, UUID code);
    void unverify(Email email);
}
