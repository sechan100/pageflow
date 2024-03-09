package org.pageflow.infra.email;

/**
 * @author : sechan
 */
public interface EmailSender {
    void sendEmail(EmailRequest emailRequest);
}
