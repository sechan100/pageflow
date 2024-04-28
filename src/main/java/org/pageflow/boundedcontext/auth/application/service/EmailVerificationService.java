package org.pageflow.boundedcontext.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.adapter.in.web.EmailVerificationWebAdapter;
import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
import org.pageflow.boundedcontext.common.value.Email;
import org.pageflow.boundedcontext.email.core.SendMailCmd;
import org.pageflow.boundedcontext.email.core.SendMailPort;
import org.pageflow.global.api.code.Code1;
import org.pageflow.global.property.AppProps;
import org.pageflow.shared.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@UseCase
@Transactional
@RequiredArgsConstructor
public class EmailVerificationService implements EmailVerificationUseCase {
    private final AppProps props;
    private final EmailVerificationPersistencePort emailVerificationPersistencePort;
    private final SendMailPort sendMailPort;

    @Override
    public void sendVerificationEmail(Email email) {
        EmailVerification ev = EmailVerification.apply(email);
        emailVerificationPersistencePort.save(ev);

        // 이메일 비동기 전송
        SendMailCmd cmd = SendMailCmd.builder()
            .to(email.toString())
            .from(props.email.from.noReply, props.email.from.defaultFromName)
            .subject("이메일 인증 요청")
            .templatePath("email-verification")
            .addVar("email", email.toString())
            .addVar("authCode", ev.getAuthCode().toString())
            .addVar("evId", ev.getId().toString())
            .addVar("serverHost", props.site.baseUrl)
            .addVar("verificationUri", EmailVerificationWebAdapter.EMAIL_VERIFICATION_URI)
            .build();

        sendMailPort.sendEmail(cmd);
    }

    @Override
    public void verify(EmailVerification.Id evId, UUID code) {
        EmailVerification ev = emailVerificationPersistencePort.load(evId)
            .orElseThrow(() -> Code1.APPLIED_EMAIL_VERIFICATION_NOT_FOUND.fire());
        ev.verify(code);
        emailVerificationPersistencePort.save(ev);
    }

    @Override
    public void unverify(Email email) {
        EmailVerification ev = EmailVerification.apply(email);
        ev.unverify();
        emailVerificationPersistencePort.save(ev);
    }
}
