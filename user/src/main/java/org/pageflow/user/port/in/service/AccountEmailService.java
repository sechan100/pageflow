package org.pageflow.user.port.in.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.email.port.*;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.entity.EmailVerificationRequest;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.pageflow.user.port.out.entity.EmailVerificationRequestPersistencePort;
import org.pageflow.user.port.out.entity.UserPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountEmailService {
  private final UserPersistencePort userPersistencePort;
  private final ApplicationProperties properties;
  private final EmailVerificationRequestPersistencePort verificationPersistencePort;
  private final SendMailPort sendMailPort;
  private final EmailTemplateProcessor emailTemplateProcessor;


  public FieldValidationResult validate(String email) {
    FieldValidator<String> validator = new FieldValidator<>("email", email)
      .email()
      .rule(e -> !userPersistencePort.existsByEmailAndIsEmailVerifiedIsTrue(e), FieldReason.DUPLICATED, "이미 사용중인 이메일입니다.");
    return validator.validate();
  }

  /**
   * 인증 이메일을 발송한다.
   *
   * @param uid             만약 같은 uid로 먼저 발송된 인증요청이 있다면 삭제 후 새로 생성한다.
   * @param email           인증할 이메일. 반드시 uid의 사용자의 이메일일 필요는 없다.
   * @param verificationUri 이메일 인증 링크
   * @return
   * @code FIELD_VALIDATION_ERROR: 이메일 유효성 검사{@link AccountEmailService#validate(String)}에서 실패한 경우
   * @code FAIL_TO_SEND_MAIL: 메일 전송 중 오류가 발생한 경우
   * @code EMAIL_ALREADY_VERIFIED: 이미 인증된 본인의 이메일로 다시 인증요청을 보내려고 시도한 경우
   */
  public Result sendVerificationMail(UID uid, String email, String verificationUri) {
    // 새로운 request 생성
    EmailVerificationRequest evRequest = EmailVerificationRequest.of(uid, email);

    // 인증메일 발송전에 이메일 유효성 한번 더 검사
    FieldValidationResult validation = validate(email);
    if(!validation.isValid()) {
      /**
       * validation에서 DUPLICATED로 실패했는데,
       * 그 이메일이 다른 사용자가 아니라 본인의 이메일이면서 인증된 이메일인 경우,
       * 조금 더 구체적인 에러코드로 바꿔서 반환해준다.
       */
      if(validation.getInvalidFields().get(0).getReason() == FieldReason.DUPLICATED) {
        User user = userPersistencePort.findById(uid.getValue()).get();
        if(user.getEmail().equals(email) && user.getIsEmailVerified()) {
          return Result.unit(UserCode.EMAIL_ALREADY_VERIFIED);
        }
      }
      return Result.unit(CommonCode.FIELD_VALIDATION_ERROR, validation);
    }

    // 기존 request가 있다면 삭제 후 새로 생성
    String requestId = EmailVerificationRequest.generateIdFromUid(uid);
    verificationPersistencePort.deleteById(requestId);
    verificationPersistencePort.persist(evRequest);

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
    return sendMailPort.sendEmail(mailRequest);
  }

  /**
   * 이메일을 인증하고 변경한다.
   * {@link AccountEmailService#sendVerificationMail(UID, String, String)}로 먼저 이메일 인증 요청을 보내야한다.
   * uid를 key로하여 서버에 저장한 인증요청을 찾고, 그 요청의 인증코드와 이메일이 cmd의 그것과 일치하는지 확인한다.
   * 일치한다면 이메일을 인증하면서 인증된 이메일로 변경한다.(기존 이메일과 동일한 경우 신경쓰지 않아도 됨)
   *
   * @return
   * @code EMAIL_VERIFICATION_EXPIRED: 인증 요청이 존재하지 않거나 만료된 경우
   * @code EMAIL_VERIFICATION_ERROR: 이메일 또는 인증코드가 일치하지 않는 경우
   */
  public Result verify(EmailVerificationCmd cmd) {
    UID uid = cmd.getUid();
    String id = EmailVerificationRequest.generateIdFromUid(uid);
    Optional<EmailVerificationRequest> evOpt = verificationPersistencePort.findById(id);

    if(evOpt.isEmpty()) {
      return Result.unit(UserCode.EMAIL_VERIFICATION_EXPIRED);
    }

    EmailVerificationRequest ev = evOpt.get();
    String email = ev.getData().getEmail();
    UUID authCode = ev.getData().getAuthCode();

    // 이메일 일치
    if(!email.equals(cmd.getEmail())) {
      return Result.unit(UserCode.EMAIL_VERIFICATION_ERROR);
    }
    // 인증코드 일치
    if(!authCode.equals(cmd.getAuthCode())) {
      return Result.unit(UserCode.EMAIL_VERIFICATION_ERROR);
    }
    // 인증처리 및 ev 삭제
    User user = userPersistencePort.findById(uid.getValue()).orElseThrow();
    user.verifyAndChangeEmail(email);
    verificationPersistencePort.delete(ev);
    return Result.ok();
  }

}
