package org.pageflow.user.port.out;

import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.User;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadAccountPort {
  Optional<User> load(String username);

  Optional<User> load(UID uid);
}
