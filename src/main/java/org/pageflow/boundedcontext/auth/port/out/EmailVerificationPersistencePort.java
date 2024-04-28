package org.pageflow.boundedcontext.auth.port.out;

import org.pageflow.boundedcontext.auth.domain.EmailVerification;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface EmailVerificationPersistencePort {
    void save(EmailVerification emailVerification);
    Optional<EmailVerification> load(EmailVerification.Id id);
}
