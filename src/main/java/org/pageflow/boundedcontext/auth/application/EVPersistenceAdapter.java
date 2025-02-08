package org.pageflow.boundedcontext.auth.application;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.EmailVarification;
import org.pageflow.boundedcontext.auth.domain.EmailVerification;
import org.pageflow.boundedcontext.auth.persistence.EmailVarificationRepository;
import org.pageflow.boundedcontext.auth.port.out.EmailVerificationPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.shared.annotation.PersistenceAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
@PersistenceAdapter
@Transactional
@RequiredArgsConstructor
public class EVPersistenceAdapter implements EmailVerificationPersistencePort {
  private final EmailVarificationRepository emailVarificationRepository;


  @Override
  public EmailVerification save(EmailVerification emailVerification) {
    EmailVarification entity = toEntity(emailVerification);
    emailVarificationRepository.save(entity);
    return emailVerification;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<EmailVerification> load(UID id) {
    return emailVarificationRepository.findById(id.toLong())
      .map(entity ->
        new EmailVerification(
          id,
          Email.from(entity.getEmail()),
          UUID.fromString(entity.getAuthCode())
        )
      );
  }

  @Override
  public void delete(EmailVerification ev) {
    emailVarificationRepository.deleteById(ev.getUid().toLong());
  }


  private EmailVarification toEntity(EmailVerification emailVerification) {
    return EmailVarification.builder()
      .uid(emailVerification.getUid().toLong())
      .email(emailVerification.getEmail().toString())
      .authCode(emailVerification.getAuthCode().toString())
      .build();
  }
}
