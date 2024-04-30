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
    Optional<User> load(UID uid);
    void save(User user);
    User signup(SignupCmd cmd);

    boolean isExist(Username username);
    boolean isExist(Email email);
}
