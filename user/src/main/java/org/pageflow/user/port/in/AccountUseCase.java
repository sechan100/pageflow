package org.pageflow.user.port.in;

import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.AccountDto;

/**
 * @author : sechan
 */
public interface AccountUseCase {

  /**
   * 인증요청 이메일을 전송한다.
   * 사용자의 현재 이메일로 요청을 전송하고, 만약 해당 이메일이 이미 인증된 상태라면 결과코드를 반환한다.
   * @param uid
   * @param verificationUri
   * @return
   */
  Result sendVerificationMail(UID uid, String verificationUri);

  /**
   * 이메일을 인증한다. {@link AccountUseCase#sendVerificationMail(UID, String)}로
   * 먼저 이메일 인증 요청을 생성하고, cmd의 값이 이와 일치할 경우 인증에 성공한다.
   * @param cmd
   * @return
   */
  Result verifyEmail(EmailVerificationCmd cmd);

  /**
   *
   * @param uid
   * @param email
   * @param verificationUri
   * @return
   */
  Result sendVerificationMailForChangeEmail(UID uid, String email, String verificationUri);

  /**
   * 또는 {@link AccountUseCase#sendVerificationMailForChangeEmail(UID, String, String)}로
   * 먼저 이메일 변경 및 인증 요청을 생성하고, cmd의 값이 이와 일치할 경우 이메일을 인증시킴과 동시에 변경한다.
   *
   * 만약 {@link AccountUseCase#sendVerificationMail(UID, String)} (UID, String, String)}로
   * 인증 메일을 전송하고 해당 함수로 인증시켰을 경우라도 결과적으로 {@link AccountUseCase#verifyEmail(EmailVerificationCmd)}와 동일한 결과를 반환한다.
   * @param cmd
   * @return
   */
  Result verifyAndChangeEmail(EmailVerificationCmd cmd);

  /**
   * 비밀번호를 변경한다.
   * @param uid
   * @param currentPassword
   * @param newPassword
   * @return
   */
  Result<AccountDto> changePassword(UID uid, String currentPassword, String newPassword);
}
