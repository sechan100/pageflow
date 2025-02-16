package org.pageflow.user.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserEmailService {
  private final AccountPersistencePort accountPersistencePort;


  public FieldValidationResult validate(String email) {
    FieldValidator<String> validator = new FieldValidator<>("email", email)
      .email()
      .rule(e -> !accountPersistencePort.existsByEmail(e), FieldReason.DUPLICATED, "이미 사용중인 이메일입니다.");

    return validator.validate();
  }

}
