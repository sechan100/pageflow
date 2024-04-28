package org.pageflow.boundedcontext.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.domain.Email;

/**
 * @author : sechan
 */
@AllArgsConstructor
public class Account {
    @Getter
    private UID uid;
    private String username;
    private EncryptedPassword password;
    @Getter
    private Email email;
    private boolean isEmailVerified;
    private RoleType role;

    public boolean passwordMatches(String rawPassword) {
        return password.matches(rawPassword);
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public void unverifyEmail() {
        this.isEmailVerified = false;
    }
}
