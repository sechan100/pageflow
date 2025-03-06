package org.pageflow.user.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.email.port.*;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.domain.entity.EmailVerificationRequest;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.pageflow.user.port.out.entity.EmailVerificationRequestPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
  private final AccountPersistencePort accountPersistencePort;
  private final ApplicationProperties properties;
  private final EmailVerificationRequestPersistencePort verificationPersistencePort;
  private final SendMailPort sendMailPort;
  private final EmailTemplateProcessor emailTemplateProcessor;

  public FieldValidationResult validate(String email) {
    FieldValidator<String> validator = new FieldValidator<>("email", email)
      .email()
      .rule(e -> !accountPersistencePort.existsByEmailAndEmailVerifiedIsTrue(e), FieldReason.DUPLICATED, "이미 사용중인 이메일입니다.");
    return validator.validate();
  }

  public void sendVerificationEmail(UID uid, String verificationUri) {
    // 새로운 request 생성
    Account account = accountPersistencePort.findById(uid.getValue()).get();
    String email = account.getEmail();
    EmailVerificationRequest evRequest = EmailVerificationRequest.of(uid, email);

    // 기존 request가 있다면 삭제
    String requestId = EmailVerificationRequest.generateIdFromUid(uid);
    verificationPersistencePort.deleteById(requestId);

    // template 파싱
    EmailTemplate template = new EmailTemplate("/email-verification-request");
    template.setVariable("serverBaseUrl", properties.site.baseUrl);
    template.setVariable("verificationUri", verificationUri);
    template.setVariable("email", email);
    template.setVariable("uid", uid);
    template.setVariable("authCode", evRequest.getData().getAuthCode());
    template.setVariable("supportEmail", properties.email.sender.support);
    EmailContent content = emailTemplateProcessor.process(template);

    MailRequest mailRequest = MailRequest.builder()
      .to(email)
      .from(properties.email.sender.noReply)
      .subject("이메일 인증 요청")
      .content(content)
      .build();
    sendMailPort.sendEmail(mailRequest);
  }

  public void verify(EmailVerificationCmd cmd) {
    UID uid = cmd.getUid();
    String id = EmailVerificationRequest.generateIdFromUid(uid);
    EmailVerificationRequest ev = verificationPersistencePort.findById(id).get();
    Account account = accountPersistencePort.findById(uid.getValue()).get();

    // 이메일 일치
    if(!account.getEmail().equals(cmd.getEmail())){
      throw new ProcessResultException(UserCode.EMAIL_VERIFICATION_ERROR);
    }
    // 인증코드 일치
    if(!ev.getData().getAuthCode().equals(cmd.getAuthCode())){
      throw new ProcessResultException(UserCode.EMAIL_VERIFICATION_ERROR);
    }
    // 인증처리 및 ev 삭제
    account.verifyEmail();
    verificationPersistencePort.delete(ev);
  }

}
