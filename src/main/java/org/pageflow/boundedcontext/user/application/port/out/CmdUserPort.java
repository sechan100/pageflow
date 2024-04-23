package org.pageflow.boundedcontext.user.application.port.out;

import org.pageflow.boundedcontext.user.application.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.domain.User;

/**
 * @author : sechan
 */
public interface CmdUserPort {
    void save(User user);
    User signup(SignupCmd cmd);
}
