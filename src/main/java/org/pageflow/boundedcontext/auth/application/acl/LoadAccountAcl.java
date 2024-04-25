package org.pageflow.boundedcontext.auth.application.acl;

import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.user.domain.UID;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadAccountAcl {
    Optional<Account> loadAccount(String username);
    Optional<Account> loadAccount(UID uid);
}
