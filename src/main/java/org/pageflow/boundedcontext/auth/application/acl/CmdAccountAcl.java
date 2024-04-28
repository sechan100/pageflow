package org.pageflow.boundedcontext.auth.application.acl;

import org.pageflow.boundedcontext.auth.domain.Account;

/**
 * @author : sechan
 */
public interface CmdAccountAcl {
    Account save(Account account);
}
