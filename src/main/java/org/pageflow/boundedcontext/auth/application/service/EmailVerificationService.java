package org.pageflow.boundedcontext.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.boundedcontext.auth.port.in.UserEmail;
import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
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

    @Override
    public void sendVerificationEmail(UserEmail userEmail) {

    }

    @Override
    public void verify(UserEmail userEmail, UUID code) {

    }

    @Override
    public void unverify(UserEmail userEmail) {

    }
}
