package org.pageflow.boundedcontext.auth.domain;

import lombok.Value;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * @author : sechan
 */
@Value
public class Account {
    private final UID uid;
    private final String username;
    private final EncryptedPassword password;
    private final String email;
    private final RoleType role;

    public boolean passwordMatches(String rawPassword) {
        return password.matches(rawPassword);
    }

}
