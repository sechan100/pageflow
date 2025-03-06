package org.pageflow.email.port;

/**
 * @author : sechan
 */
public interface EmailTemplateProcessor {
  EmailContent process(EmailTemplate emailTemplate);
}
