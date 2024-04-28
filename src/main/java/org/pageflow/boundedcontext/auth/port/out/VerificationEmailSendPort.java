package org.pageflow.boundedcontext.auth.port.out;

/**
 * @author : sechan
 */
public interface VerificationEmailSendPort {
    void sendVerificationEmail(VerificationEmailSendCmd cmd);
}
