package org.pageflow.user.port.in.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.result.NullData;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.Password;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.dto.AccountDto;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.pageflow.user.port.in.UserUseCase;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements AccountUseCase, UserUseCase {
  private final AccountPersistencePort accountPersistencePort;
  private final UsernameValidator usernameValidator;
  private final AccountEmailService accountEmailService;


  /**
   *
   * @param uid
   * @param verificationUri
   * @return
   * @code FIELD_VALIDATION_ERROR: 이메일 유효성 검사{@link AccountEmailService#validate(String)}에서 실패한 경우
   * @code FAIL_TO_SEND_MAIL: 메일 전송 중 오류가 발생한 경우
   * @code EMAIL_ALREADY_VERIFIED: 이미 인증된 본인의 이메일로 다시 인증요청을 보내려고 시도한 경우
   */
  @Override
  public Result sendVerificationMail(UID uid, String verificationUri) {
    Account account = _ensureAccount(uid);
    return accountEmailService.sendVerificationMail(uid, account.getEmail(), verificationUri);
  }

  /**
   *
   * @param uid
   * @param email
   * @param verificationUri
   * @return
   * @code FIELD_VALIDATION_ERROR: 이메일 유효성 검사{@link AccountEmailService#validate(String)}에서 실패한 경우
   * @code FAIL_TO_SEND_MAIL: 메일 전송 중 오류가 발생한 경우
   * @code EMAIL_ALREADY_VERIFIED: 이미 인증된 본인의 이메일로 다시 인증요청을 보내려고 시도한 경우
   */
  @Override
  public Result sendVerificationMailForChangeEmail(UID uid, String email, String verificationUri) {
    return accountEmailService.sendVerificationMail(uid, email, verificationUri);
  }

  /**
   *
   * @param cmd
   * @return
   * @code EMAIL_VERIFICATION_EXPIRED: 인증 요청이 존재하지 않거나 만료된 경우
   * @code EMAIL_VERIFICATION_ERROR: 이메일 또는 인증코드가 일치하지 않는 경우
   */
  @Override
  public Result<NullData> verifyEmail(EmailVerificationCmd cmd) {
    return accountEmailService.verify(cmd);
  }

  /**
   *
   * @param cmd
   * @return
   * @code EMAIL_VERIFICATION_EXPIRED: 인증 요청이 존재하지 않거나 만료된 경우
   * @code EMAIL_VERIFICATION_ERROR: 이메일 또는 인증코드가 일치하지 않는 경우
   */
  @Override
  public Result verifyAndChangeEmail(EmailVerificationCmd cmd) {
    Result verifyResult = accountEmailService.verify(cmd);
    if(verifyResult.isFailure()) return verifyResult;

    Account account = _ensureAccount(cmd.getUid());
    account.changeVerifiedEmail(cmd.getEmail());
    return Result.success();
  }

  @Override
  public Optional<UserDto> queryUser(UID uid) {
    Optional<Account> accountOptional = accountPersistencePort.findWithProfileById(uid.getValue());
    return accountOptional.map(UserDto::from);
  }

  /**
   *
   * @param uid
   * @param currentPassword
   * @param newPassword
   * @return Result
   * @code FIELD_VALIDATION_ERROR : 새로운 비밀번호가 유효하지 않을 때
   * @code BAD_CREDENTIALS: currentPassword가 일치하지 않는 경우
   * @code PASSWORD_SAME_AS_BEFORE: 새로운 비밀번호가 기존 비밀번호와 동일한 경우
   */
  @Override
  public Result<AccountDto> changePassword(UID uid, String currentPassword, String newPassword) {
    Account account = _ensureAccount(uid);
    Result<Password> encryptResult = Password.encrypt(newPassword);
    if(encryptResult.isFailure()){
      return (Result) encryptResult;
    }
    Result<NullData> changeResult = account.changePassword(currentPassword, encryptResult.getSuccessData());
    if(changeResult.isFailure()){
      return (Result) changeResult;
    }
    AccountDto accountDto = AccountDto.from(account);
    return Result.success(accountDto);
  }

  private Account _ensureAccount(UID uid) {
    return accountPersistencePort.findById(uid.getValue()).orElseThrow();
  }
}
