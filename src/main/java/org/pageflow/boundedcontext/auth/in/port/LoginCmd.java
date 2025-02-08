package org.pageflow.boundedcontext.auth.in.port;

import lombok.Value;
import org.pageflow.boundedcontext.auth.domain.Account;

/**
 * @author : sechan
 */
@Value
public class LoginCmd {
  Account aleadyAuthedAccount;
}
