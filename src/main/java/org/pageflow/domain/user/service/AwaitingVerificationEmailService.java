package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.exception.nosuchentity.NoSuchEntityException;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.pageflow.domain.user.repository.AwaitingVerificationEmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AwaitingVerificationEmailService {

    private final AwaitingVerificationEmailRepository emailCacheRepository;

    public AwaitingEmailVerificationRequest save(AwaitingEmailVerificationRequest awaitingVerificationEmail) {
        return emailCacheRepository.save(awaitingVerificationEmail);
    }

    public boolean existsById(String email) {
        return emailCacheRepository.existsById(email);
    }

    public AwaitingEmailVerificationRequest findById(String email) {
        return emailCacheRepository.findById(email).orElseThrow(
                () -> new NoSuchEntityException(AwaitingEmailVerificationRequest.class)
        );
    }

    public void delete(String email) {
        emailCacheRepository.deleteById(email);
    }

    @Transactional
    public AwaitingEmailVerificationRequest verify(String email) {
        AwaitingEmailVerificationRequest awaitingEmailVerificationRequest = findById(email);
        awaitingEmailVerificationRequest.setVerified(true);
        return save(awaitingEmailVerificationRequest);
    }
}
