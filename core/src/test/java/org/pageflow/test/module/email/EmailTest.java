package org.pageflow.test.module.email;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.pageflow.email.port.EmailTemplateProcessor;
import org.pageflow.email.port.SendMailPort;
import org.pageflow.test.e2e.PageflowIntegrationTest;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class EmailTest {
  private final SendMailPort sendMailPort;
  private final EmailTemplateProcessor emailTemplateProcessor;

  @Test
  void sendEmail() {
//    EmailTemplate emailTemplate = new EmailTemplate("/test");
//    emailTemplate.setVariable("message", "테스트 이메일 입니다. message variable 주입 테스트 문자열");
//    EmailContent content = emailTemplateProcessor.process(emailTemplate);
//    MailRequest request = MailRequest.builder()
//      .to("sechan100@gmail.com")
//      .from("verify@pageflow.org")
//      .subject("Pageflow Email Test입니다.")
//      .content(content)
//      .build();
//    sendMailPort.sendEmail(request);
  }
}
