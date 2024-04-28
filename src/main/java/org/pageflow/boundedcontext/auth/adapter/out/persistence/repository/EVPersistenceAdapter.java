package org.pageflow.boundedcontext.auth.adapter.out.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.adapter.out.persistence.entity.EVRedisEntity;
import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
import org.pageflow.shared.annotation.PersistenceAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@PersistenceAdapter
@Transactional
@RequiredArgsConstructor
public class EVPersistenceAdapter implements EmailVerificationPersistencePort {
    private final EVRedisRepository evRedisRepository;


    @Override
    public void save(EmailVerification emailVerification) {
        // 인증되지 않은 경우
        if(!emailVerification.isVerified()){
            evRedisRepository.save(toEntity(emailVerification));
        }

    }

    @Override
    public Optional<EmailVerification> load(EmailVerification.Id id) {
        return Optional.empty();
    }

    private EVRedisEntity toEntity(EmailVerification emailVerification) {
        return EVRedisEntity.builder()
            .id(emailVerification.getId().getValue())
            .email(emailVerification.getEmail())
            .authCode(emailVerification.getAuthCode().toString())
            .build();
    }
}
