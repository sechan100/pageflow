package org.pageflow.email;

/**
 * @author : sechan
 */
public interface SendMailPort {
  void sendEmail(SendMailCmd sendMailCmd);
}
