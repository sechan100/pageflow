package org.pageflow.boundedcontext.user.port.out;

import org.pageflow.boundedcontext.user.domain.User;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;

/**
 * @author : sechan
 */
public interface CmdUserPort {
    void save(User user);
    User signup(SignupCmd cmd);
}
