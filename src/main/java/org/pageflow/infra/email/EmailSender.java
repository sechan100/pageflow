package org.pageflow.infra.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

    private final JavaMailSender javaMailSender;


    public void sendMail(EmailRequest request, String template) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(request.getTo());
            mimeMessageHelper.setSubject(request.getSubject());
            mimeMessageHelper.setFrom(request.getFrom());
            mimeMessageHelper.setText(template, true);

            // ### mail send ###
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            log.info("Success To Send Email!");


        } catch (MessagingException e) {
            log.info("Fail To Send Email!");

        } catch (MailException e) {
            log.info("Fail To Send Email For Authentication!");
        }
    }

}