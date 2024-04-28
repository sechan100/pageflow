package org.pageflow.boundedcontext.auth.port.out;

import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.shared.type.TSID;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface EmailVerificationPersistencePort {
    void save(EmailVerification emailVerification);
    Optional<EmailVerification> load(TSID emailVerificationId);
}
