package org.pageflow.boundedcontext.auth.port.in;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface EmailVerificationUseCase {
    void sendVerificationEmail(UserEmail userEmail);
    void verify(UserEmail userEmail, UUID code);
    void unverify(UserEmail userEmail);
}
