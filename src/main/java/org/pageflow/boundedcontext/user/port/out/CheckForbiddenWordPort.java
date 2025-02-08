package org.pageflow.boundedcontext.user.port.out;

import org.pageflow.boundedcontext.user.domain.Penname;
import org.pageflow.boundedcontext.user.domain.Username;

/**
 * @author : sechan
 */
public interface CheckForbiddenWordPort {
  void checkPennameContainsForbiddenWord(Penname penname);

  void checkUsernameContainsForbiddenWord(Username username);
}
