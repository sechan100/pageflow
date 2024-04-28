package org.pageflow.boundedcontext.user.port.out;

import org.pageflow.boundedcontext.common.value.Email;
import org.pageflow.boundedcontext.user.domain.Username;

/**
 * @author : sechan
 */
public interface UserExistenceCheckPort {
    boolean isExist(Username username);
    boolean isExist(Email email);
}