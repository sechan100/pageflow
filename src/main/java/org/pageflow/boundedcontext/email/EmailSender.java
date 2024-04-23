package org.pageflow.boundedcontext.email;

/**
 * @author : sechan
 */
public interface EmailSender {
    void sendEmail(EmailRequest emailRequest);
}
