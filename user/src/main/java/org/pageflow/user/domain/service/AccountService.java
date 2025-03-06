package org.pageflow.user.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.dto.AccountDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {
  private final AccountPersistencePort accountPersistencePort;
  private final UsernameValidator usernameValidator;
  private final AccountEmailService accountEmailService;


  @Override
  public AccountDto changeEmail(UID uid, String email) {
    FieldValidationResult validation = accountEmailService.validate(email);
    if(!validation.isValid()){
      throw new FieldValidationException(validation);
    }

    Account account = load(uid);
    account.changeEmail(email);
    return AccountDto.from(account);
  }


  private Account load(UID uid) {
    return accountPersistencePort.findById(uid.getValue()).orElseThrow();
  }

}
