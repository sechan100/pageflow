package org.pageflow.boundedcontext.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
import org.pageflow.boundedcontext.auth.port.out.VerificationEmailSendCmd;
import org.pageflow.boundedcontext.auth.port.out.VerificationEmailSendPort;
import org.pageflow.boundedcontext.common.value.Email;
import org.pageflow.global.api.code.Code1;
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
    private final EmailVerificationPersistencePort emailVerificationPersistencePort;
    private final VerificationEmailSendPort emailSendPort;

    @Override
    public void sendVerificationEmail(Email email) {
        EmailVerification ev = EmailVerification.apply(email);
        emailVerificationPersistencePort.save(ev);

        // 이메일 비동기 전송
        emailSendPort.sendVerificationEmail(
            new VerificationEmailSendCmd(
                ev.getId().toString(),
                email.toString(),
                ev.getAuthCode().toString()
            )
        );
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
