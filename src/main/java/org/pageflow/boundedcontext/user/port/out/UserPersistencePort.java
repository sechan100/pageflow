package org.pageflow.boundedcontext.user.port.out;

import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.User;
import org.pageflow.boundedcontext.user.domain.Username;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface UserPersistencePort {
  Optional<User> loadUser(UID uid);

  Optional<User> loadUser(Username username);

  User saveUser(User user);

  User signup(SignupCmd cmd);

  boolean isUserExistByEmail(Username username);

  boolean isUserExistByEmail(Email email);
}
