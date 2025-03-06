package org.pageflow.email.port;

/**
 * @author : sechan
 */
public interface SendMailPort {
  void sendEmail(MailRequest mailRequest);
}
