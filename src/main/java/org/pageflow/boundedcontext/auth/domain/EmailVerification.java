package org.pageflow.boundedcontext.auth.domain;

import org.pageflow.boundedcontext.common.value.Email;
import org.pageflow.global.api.code.Code1;
import org.pageflow.shared.type.SingleValueWrapper;
import org.pageflow.shared.type.TSID;

import java.util.UUID;

/**
 * @author : sechan
 */
public class EmailVerification {
    private final EmailVerification.Id id;
    private final String email;
    private final UUID authCode;
    private boolean isVerified;

    private EmailVerification(EmailVerification.Id id, Email email, UUID authCode) {
        this.id = id;
        this.email = email.getValue();
        this.isVerified = email.isVerified();
        this.authCode = authCode;
    }

    public static EmailVerification apply(Email email) {
        return new EmailVerification(
            new EmailVerification.Id(TSID.Factory.getTsid()),
            email,
            java.util.UUID.randomUUID()
        );
    }

    public void verify(UUID authCode) {
        if(!this.authCode.equals(authCode)){
            throw Code1.EMAIL_VERIFICATION_AUTH_CODE_MISMATCH.fire();
        }
        this.isVerified = true;
    }

    public void unverify() {
        this.isVerified = false;
    }

    // getters
    public EmailVerification.Id getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UUID getAuthCode() {
        return authCode;
    }

    public boolean isVerified() {
        return isVerified;
    }



    public static final class Id extends SingleValueWrapper<TSID> {
        public Id(TSID value) {
            super(value);
        }
    }
}
