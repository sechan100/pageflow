package org.pageflow.user.port.in.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.pageflow.user.port.out.entity.UserPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SignupFacade implements SignupUseCase {
  private final UserPersistencePort userPersistencePort;
  private final UsernameValidator usernameValidator;
  private final PennameValidator pennameValidator;
  private final AccountEmailService accountEmailService;

  @Override
  public UserDto signup(SignupCmd cmd) {
    // 검증
    var usernameValidation = usernameValidator.validate(cmd.getUsername());
    var pennameValidation = pennameValidator.validate(cmd.getPenname());
    var emailValidation = accountEmailService.validate(cmd.getEmail());
    var validation = FieldValidationResult.combine(
      usernameValidation,
      pennameValidation,
      emailValidation
    );
    if(!validation.isValid()) {
      throw new FieldValidationException(validation);
    }

    // 생성
    User user = User.create(
      UUID.randomUUID(), // uid
      cmd.getUsername(), // username
      cmd.getPassword(), // password
      cmd.getEmail(), // email
      cmd.getPenname(), // penname
      cmd.getProfileImageUrl(), // profileImageUrl
      cmd.getProvider(), // provider
      cmd.getRole() // role
    );
    User savedUser = userPersistencePort.persist(user);
    return new UserDto(savedUser);
  }
}
