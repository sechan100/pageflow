package org.pageflow.boundedcontext.email.core;

/**
 * @author : sechan
 */
public interface SendMailPort {
  void sendEmail(SendMailCmd sendMailCmd);
}
