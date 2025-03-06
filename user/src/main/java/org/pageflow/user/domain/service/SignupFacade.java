package org.pageflow.user.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.domain.entity.Profile;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.pageflow.user.port.out.entity.ProfilePersistencePort;
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
  private final AccountPersistencePort accountPersistencePort;
  private final ProfilePersistencePort profilePersistencePort;
  private final UsernameValidator usernameValidator;
  private final PennameValidator pennameValidator;
  private final EmailService emailService;

  @Override
  public UserDto signup(SignupCmd cmd) {
    // 검증
    var usernameValidation = usernameValidator.validate(cmd.getUsername());
    var pennameValidation = pennameValidator.validate(cmd.getPenname());
    var emailValidation = emailService.validate(cmd.getEmail());
    var validation = FieldValidationResult.combine(
      usernameValidation,
      pennameValidation,
      emailValidation
    );
    if(!validation.isValid()){
      throw new FieldValidationException(validation);
    }

    // 생성
    UUID uid = UUID.randomUUID();
    // 프로필
    Profile profile = Profile.builder()
      .id(uid)
      .penname(cmd.getPenname())
      .profileImageUrl(cmd.getProfileImageUrl())
      .build();
    // 계정
    Account account = Account.builder()
      .id(uid)
      .username(cmd.getUsername())
      .password(cmd.getPassword().toString())
      .email(cmd.getEmail())
      .emailVerified(false)
      .provider(cmd.getProvider())
      .role(cmd.getRole())
      .build();

    // 연관관계의 주인인 Profile이 @MapsId를 사용해서 Account의 PK를 참조하기 때문에, Account가 먼저 영속화 되어야한다.
    Account savedAccount = accountPersistencePort.persist(account);
    savedAccount.associateProfile(profile);
    Profile savedProfile = profilePersistencePort.persist(profile);

    return UserDto.from(savedAccount);
  }
}
