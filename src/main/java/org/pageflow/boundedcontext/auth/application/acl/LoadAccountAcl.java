package org.pageflow.boundedcontext.auth.application.acl;

import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.common.value.UID;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadAccountAcl {
    Optional<Account> load(String username);
    Optional<Account> load(UID uid);
}
