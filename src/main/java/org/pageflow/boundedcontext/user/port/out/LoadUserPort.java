package org.pageflow.boundedcontext.user.port.out;

import org.pageflow.boundedcontext.user.domain.UID;
import org.pageflow.boundedcontext.user.domain.User;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadUserPort {
    Optional<User> load(UID uid);
}
