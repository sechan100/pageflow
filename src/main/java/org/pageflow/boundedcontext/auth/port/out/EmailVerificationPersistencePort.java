package org.pageflow.boundedcontext.auth.port.out;

import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.common.value.UID;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface EmailVerificationPersistencePort {
    EmailVerification save(EmailVerification emailVerification);
    Optional<EmailVerification> load(UID id);
    void delete(EmailVerification ev);
}
