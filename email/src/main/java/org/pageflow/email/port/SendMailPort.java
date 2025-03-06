package org.pageflow.email.port;

import org.pageflow.common.result.Result;

/**
 * @author : sechan
 */
public interface SendMailPort {
  Result sendEmail(MailRequest mailRequest);
}
