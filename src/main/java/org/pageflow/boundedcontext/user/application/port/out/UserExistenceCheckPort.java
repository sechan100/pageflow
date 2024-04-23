package org.pageflow.boundedcontext.user.application.port.out;

import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.Username;

/**
 * @author : sechan
 */
public interface UserExistenceCheckPort {
    boolean isExist(Username username);
    boolean isExist(Email email);
}