package org.pageflow.boundedcontext.auth.domain;

import lombok.Value;
import org.pageflow.boundedcontext.common.value.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class EmailVerification {
    private final UID uid;
    private final UUID authCode;

    public EmailVerification(UID uid, UUID authCode) {
        this.uid = uid;
        this.authCode = authCode;
    }

    public static EmailVerification apply(UID uid) {
        return new EmailVerification(
            uid,
            java.util.UUID.randomUUID()
        );
    }
}
