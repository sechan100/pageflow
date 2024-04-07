package org.pageflow.shared.infra.email;

/**
 * @author : sechan
 */
public interface EmailSender {
    void sendEmail(EmailRequest emailRequest);
}
