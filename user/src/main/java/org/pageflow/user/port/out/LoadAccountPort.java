package org.pageflow.user.port.out;

import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Account;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadAccountPort {
  Optional<Account> load(String username);
  Optional<Account> load(UID uid);
}
