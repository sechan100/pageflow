package org.pageflow.boundedcontext.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.adapter.in.web.EmailVerificationWebAdapter;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.email.core.SendMailCmd;
import org.pageflow.boundedcontext.email.core.SendMailPort;
import org.pageflow.global.api.code.Code1;
import org.pageflow.global.api.code.Code3;
import org.pageflow.global.property.AppProps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationService implements EmailVerificationUseCase {
    private final AppProps props;
    private final EmailVerificationPersistencePort emailVerificationPersistencePort;
    private final AccountPersistencePort accountPersistencePort;
    private final SendMailPort sendMailPort;

    @Override
    public void sendVerificationEmail(UID uid) {
        EmailVerification ev = emailVerificationPersistencePort.load(uid)
            .orElseGet(() -> {
                EmailVerification newEV = EmailVerification.apply(uid);
                return emailVerificationPersistencePort.save(newEV);
            });

        String email = accountPersistencePort.loadAccount(ev.getUid())
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다."))
            .getEmail().toString();

        // 이메일 비동기 전송
        SendMailCmd sendMailCmd = SendMailCmd.builder()
            .to(email)
            .from(props.email.from.noReply, props.email.from.defaultFromName)
            .subject("이메일 인증 요청")
            .templatePath("/email-verification")
            .addVar("email", email)
            .addVar("authCode", ev.getAuthCode().toString())
            .addVar("uid", uid.toString())
            .addVar("serverHost", props.site.baseUrl)
            .addVar("verificationUri", EmailVerificationWebAdapter.EMAIL_VERIFICATION_URI)
            .build();
        sendMailPort.sendEmail(sendMailCmd);
    }

    @Override
    public void verify(UID uid, UUID code) {
        EmailVerification ev = emailVerificationPersistencePort.load(uid)
            .orElseThrow(() -> Code1.APPLIED_EMAIL_VERIFICATION_NOT_FOUND.fire());
        Account account = accountPersistencePort.loadAccount(ev.getUid())
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다."));

        // 인증코드 일치 확인
        if(!ev.getAuthCode().equals(code)){
            throw Code1.EMAIL_VERIFICATION_AUTH_CODE_MISMATCH.fire();
        }

        account.verifyEmail();
        emailVerificationPersistencePort.delete(ev);
        accountPersistencePort.saveAccount(account);
    }

    @Override
    public void unverify(UID uid) {
        Account account = accountPersistencePort.loadAccount(uid)
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다."));
        account.unverifyEmail();
        accountPersistencePort.saveAccount(account);
    }
}
