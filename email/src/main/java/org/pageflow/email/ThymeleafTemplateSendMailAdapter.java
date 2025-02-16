package org.pageflow.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author : sechan
 */
@Slf4j
@Service
//@RequiredArgsConstructor
public class ThymeleafTemplateSendMailAdapter implements SendMailPort {

  private static final String EMAIL_TEMPLATE_PATH_PREFIX = "/email";
//  private final ApplicationProperties props;
  private final TemplateEngine thymeleafEngine;
  private final JavaMailSender javaMailSender;

  public ThymeleafTemplateSendMailAdapter(
//    ApplicationProperties props,
    TemplateEngine thymeleafEngine,
    JavaMailSender javaMailSender
  ){
//    this.props = props;
    this.thymeleafEngine = thymeleafEngine;
    this.javaMailSender = javaMailSender;
  }

  @Async
  @Override
  public void sendEmail(SendMailCmd request) {
    // 인증 코드를 템플릿에 담아서 이메일 본문을 생성
    Context context = new Context();
    context.setVariables((Map) request.getVariables());
    String emailContent = thymeleafEngine.process(
      EMAIL_TEMPLATE_PATH_PREFIX + request.getTemplatePath(),
      context
    );
    try {
      // 메시지 생성
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setSubject(request.getSubject());
      mimeMessageHelper.setFrom(
        new InternetAddress(
          request.getFrom(),
          request.getFromName(),
          "UTF-8"
        )
      );
      mimeMessageHelper.setTo(request.getTo());
      mimeMessageHelper.setText(emailContent, true);
      // 메일 전송
      javaMailSender.send(mimeMessageHelper.getMimeMessage());
    } catch(MessagingException | MailException e){
      throw new RuntimeException(e);
    } catch(UnsupportedEncodingException e){
      throw new RuntimeException(e);
    }
  }
}








