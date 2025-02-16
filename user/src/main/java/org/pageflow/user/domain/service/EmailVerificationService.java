//package org.pageflow.user.domain.service;
//
//import lombok.RequiredArgsConstructor;
//import org.pageflow.auth.domain.AuthAccount;
//import org.pageflow.auth.domain.exception.EmailVerificationAuthCodeMisMatchException;
//import org.pageflow.auth.domain.exception.RequireSendVerificationEmailException;
//import org.pageflow.auth.port.in.EmailVerificationUseCase;
//import org.pageflow.boundedcontext.auth.domain.EmailVerification;
//import org.pageflow.user.adapter.in.web.EmailVerificationWebAdapter;
//import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
//import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
//import org.pageflow.boundedcontext.common.value.UID;
//import org.pageflow.boundedcontext.email.core.SendMailCmd;
//import org.pageflow.boundedcontext.email.core.SendMailPort;
//import org.pageflow.boundedcontext.user.domain.Email;
//import org.pageflow.global.property.AppProps;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
///**
// * @author : sechan
// */
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class EmailVerificationService implements EmailVerificationUseCase {
//  private final AppProps props;
//  private final EmailVerificationPersistencePort emailVerificationPersistencePort;
//  private final AccountPersistencePort accountPersistencePort;
//  private final SendMailPort sendMailPort;
//
//  @Override
//  public void sendVerificationEmail(UID uid) {
//    EmailVerification ev = emailVerificationPersistencePort.load(uid)
//      .orElseGet(() -> {
//        Email email = accountPersistencePort.loadAccount(uid).get().getEmail();
//        EmailVerification newEV = EmailVerification.apply(uid, email);
//        return emailVerificationPersistencePort.save(newEV);
//      });
//
//    // 이메일 비동기 전송
//    SendMailCmd sendMailCmd = SendMailCmd.builder()
//      .to(ev.getEmail().toString())
//      .from(props.email.from.noReply, props.email.from.defaultFromName)
//      .subject("이메일 인증 요청")
//      .templatePath("/email-verification")
//      .addVar("email", ev.getEmail().toString())
//      .addVar("authCode", ev.getAuthCode().toString())
//      .addVar("uid", uid.toString())
//      .addVar("serverHost", props.site.baseUrl)
//      .addVar("verificationUri", EmailVerificationWebAdapter.EMAIL_VERIFICATION_URI)
//      .build();
//    sendMailPort.sendEmail(sendMailCmd);
//  }
//
//  @Override
//  public void verify(UID uid, UUID code) {
//    EmailVerification ev = emailVerificationPersistencePort.load(uid)
//      .orElseThrow(() -> new RequireSendVerificationEmailException(uid));
//    AuthAccount account = accountPersistencePort.loadAccount(ev.getUid()).get();
//
//    // 인증코드 일치 확인
//    if(!ev.getAuthCode().equals(code)){
//      throw new EmailVerificationAuthCodeMisMatchException(code);
//    }
//
//    account.verifyEmail();
//    emailVerificationPersistencePort.delete(ev);
//    accountPersistencePort.saveAccount(account);
//  }
//
//  @Override
//  public void unverify(UID uid) {
//    AuthAccount account = accountPersistencePort.loadAccount(uid).get();
//    account.unverifyEmail();
//    accountPersistencePort.saveAccount(account);
//  }
//}
