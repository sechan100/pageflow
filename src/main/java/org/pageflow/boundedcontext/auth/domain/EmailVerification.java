package org.pageflow.boundedcontext.auth.domain;

import lombok.Value;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.domain.Email;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class EmailVerification {
    private final UID uid;
    private final Email email;
    private final UUID authCode;

    public EmailVerification(UID uid, Email email, UUID authCode) {
        this.uid = uid;
        this.email = email;
        this.authCode = authCode;
    }

    public static EmailVerification apply(UID uid, Email email) {
        return new EmailVerification(
            uid,
            email,
            java.util.UUID.randomUUID()
        );
    }
}
