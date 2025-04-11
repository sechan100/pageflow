package org.pageflow.user.port.in;

import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.UserDto;

/**
 * @author : sechan
 */
public interface AccountUseCase {

  /**
   * 인증요청 이메일을 전송한다.
   * 사용자의 현재 이메일로 요청을 전송하고, 만약 해당 이메일이 이미 인증된 상태라면 결과코드를 반환한다.
   *
   * @param uid
   * @param email
   * @param verificationUri
   * @return
   */
  Result sendVerificationMail(UID uid, String email, String verificationUri);

  /**
   * 이메일을 인증한다.
   * {@link AccountUseCase#sendVerificationMail(UID, String, String)}로 먼저 이메일 인증 요청을 생성하고,
   * cmd의 값이 이와 일치할 경우 인증에 성공한다.
   * 현재 이메일과 인증한 이메일이 다른 경우, 인증된 이메일로 변경된다.
   *
   * @param cmd
   * @return
   */
  Result verifyEmail(EmailVerificationCmd cmd);

  /**
   * 비밀번호를 변경한다.
   *
   * @param uid
   * @param currentPassword
   * @param newPassword
   * @return
   */
  Result<UserDto> changePassword(UID uid, String currentPassword, String newPassword);
}
