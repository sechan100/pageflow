package org.pageflow.shared.infra.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.pageflow.global.api.BizException;
import org.pageflow.global.api.code.GeneralCode;
import org.pageflow.global.constants.CustomProps;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class ThymeleafTemplateEmailSender implements EmailSender {
    
    private final CustomProps props;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private static final String EMAIL_TEMPLATE_PATH_PREFIX = "/email";
    
    @Override
    public void sendEmail(EmailRequest request) {
        // 인증 코드를 템플릿에 담아서 이메일 본문을 생성
        Context context = new Context();
        context.setVariables(request.getModels());
        String emailContent = templateEngine.process(
                EMAIL_TEMPLATE_PATH_PREFIX + request.getTemplate(),
                context
        );
        try {
            // 마임 메시지 생성
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setSubject(request.getSubject());
            mimeMessageHelper.setFrom(new InternetAddress(
                    request.getFrom(),
                    request.getFromName(),
                    "UTF-8")
            );
            mimeMessageHelper.setTo(request.getTo());
            mimeMessageHelper.setText(emailContent, true);
            // 메일 전송
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch(MessagingException | MailException e) {
            throw new BizException(GeneralCode.FAIL_TO_SEND_EMAIL);
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
}








