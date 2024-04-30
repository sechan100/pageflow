package org.pageflow.boundedcontext.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * @author : sechan
 */

@Getter
@AllArgsConstructor
public class User {
    private final UID uid;
    private final Username username;
    private final RoleType role;
    private Email email;
    private boolean isEmailVerified;
    private Penname penname;
    private ProfileImageUrl profileImageUrl;

    public void changeEmail(Email email) {
        this.email = email;
        this.isEmailVerified = false;
    }

    public void changePenname(Penname penname) {
        this.penname = penname;
    }

    public void changeProfileImage(ProfileImageUrl profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


}
