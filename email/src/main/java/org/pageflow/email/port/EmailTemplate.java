package org.pageflow.email.port;

import lombok.Getter;
import org.thymeleaf.context.AbstractContext;


/**
 * @author : sechan
 */
public class EmailTemplate extends AbstractContext {
  @Getter
  private final String templatePath;

  private EmailTemplate(String templatePath) {
    super();
    this.templatePath = templatePath;
  }

  public static EmailTemplate of(String templatePath) {
    return new EmailTemplate(templatePath);
  }

}
