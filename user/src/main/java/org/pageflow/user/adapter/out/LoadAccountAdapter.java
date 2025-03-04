package org.pageflow.user.adapter.out;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.port.out.LoadAccountPort;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadAccountAdapter implements LoadAccountPort {
  private final AccountPersistencePort delegate;
  private final EntityManager entityManager;

  @Override
  public Optional<Account> load(String username) {
    return delegate.findByUsername(username);
  }

  @Override
  public Optional<Account> load(UID uid) {
    return delegate.findById(uid.getValue());
  }
}
