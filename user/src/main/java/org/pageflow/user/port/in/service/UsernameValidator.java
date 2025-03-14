package org.pageflow.user.port.in.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UsernameValidator {
  public static final int MIN_LENGTH = 4;
  public static final int MAX_LENGTH = 100;
  public static final String REGEX = "^[a-zA-Z0-9-]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";
  public static final String REGEX_MESSAGE = "아이디는 영문, 숫자를 사용해서 입력해주세요.";
  public static final Collection<String> INVALID_USERNAME = Set.of(
    "admin", "administrator", "anonymous", "pageflow", "guest"
  );

  private final AccountPersistencePort accountPersistencePort;

  public FieldValidationResult validate(String username) {
    FieldValidator<String> validator = new FieldValidator<>("username", username)
      .minLength(MIN_LENGTH, "아이디는 최소 " + MIN_LENGTH + "자 이상이어야 합니다.")
      .maxLength(MAX_LENGTH, "아이디는 최대 " + MAX_LENGTH + "자 이하여야 합니다.")
      .regex(REGEX, REGEX_MESSAGE)
      .notSame(INVALID_USERNAME, w -> String.format("'%s'은(는) 사용할 수 없는 아이디입니다.", w))
      .rule(u -> !accountPersistencePort.existsByUsername(u), FieldReason.DUPLICATED, "이미 사용중인 아이디입니다.");

    return validator.validate();
  }

}
