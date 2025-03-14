package org.pageflow.email.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.result.Result;
import org.pageflow.email.application.EmailCode;
import org.pageflow.email.port.MailRequest;
import org.pageflow.email.port.SendMailPort;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendMailService implements SendMailPort {
  private final JavaMailSender javaMailSender;


  /**
   * @param request
   * @return Result
   * <ul>
   *   <li>
   *     FAIL_TO_SEND_MAIL: 메일 전송 중 오류가 발생한 경우
   *   </li>
   * </ul>
   */
  @Override
  public Result sendEmail(MailRequest request) {
    try {
      // 메시지 생성
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setSubject(request.getSubject());
      mimeMessageHelper.setFrom(new InternetAddress(request.getFrom(), request.getFromName(), "UTF-8"));
      mimeMessageHelper.setTo(request.getTo());
      mimeMessageHelper.setText(request.getEmailContent().getValue(), true);
      // 메일 전송
      javaMailSender.send(mimeMessageHelper.getMimeMessage());
      return Result.success();

    } catch(MessagingException | MailException e) {
      log.error("메일 전송 중 오류가 발생했습니다.", e);
      return Result.of(EmailCode.FAIL_TO_SEND_MAIL);

    } catch(UnsupportedEncodingException e) {
      return Result.of(EmailCode.FAIL_TO_SEND_MAIL);
    }
  }
}