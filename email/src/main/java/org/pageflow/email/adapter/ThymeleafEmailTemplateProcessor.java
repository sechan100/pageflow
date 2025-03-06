package org.pageflow.email.adapter;

import lombok.RequiredArgsConstructor;
import org.pageflow.email.port.EmailContent;
import org.pageflow.email.port.EmailTemplate;
import org.pageflow.email.port.EmailTemplateProcessor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class ThymeleafEmailTemplateProcessor implements EmailTemplateProcessor {
  private final TemplateEngine thymeleafEngine;

  @Override
  public EmailContent process(EmailTemplate emailTemplate) {
    String content = thymeleafEngine.process(
      emailTemplate.getTemplatePath(),
      emailTemplate
    );
    return EmailContent.fromProcessedTemplate(content);
  }
}
