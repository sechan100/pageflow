package org.pageflow.email.port;

import org.pageflow.common.utility.SingleValueWrapper;

/**
 * @author : sechan
 */
public class EmailContent extends SingleValueWrapper<String> {
  private EmailContent(String value) {
    super(value);
  }

  public static EmailContent fromProcessedTemplate(String value) {
    return new EmailContent(value);
  }
}
