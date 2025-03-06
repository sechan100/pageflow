package org.pageflow.email.port;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.thymeleaf.context.Context;


/**
 * @author : sechan
 */
public class EmailTemplate {
  @Getter
  @Delegate
  private final Context context;
  @Getter
  private final String templatePath;

  public EmailTemplate(String templatePath) {
    this.templatePath = templatePath;
    this.context = new Context();
  }

}
