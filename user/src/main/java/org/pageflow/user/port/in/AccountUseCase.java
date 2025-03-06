package org.pageflow.user.port.in;

import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.AccountDto;

/**
 * @author : sechan
 */
public interface AccountUseCase {
  AccountDto changeEmail(UID uid, String email);
  Result sendEmailVerificationMail(UID uid, String verificationUri);
  void verifyEmail(EmailVerificationCmd cmd);
}
