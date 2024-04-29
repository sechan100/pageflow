package org.pageflow.boundedcontext.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * @author : sechan
 */

@Getter
@AllArgsConstructor
public class User {
    private final UID uid;
    private final Username username;
    private final Email email;
    private final boolean isEmailVerified;
    private final Penname penname;
    private final ProfileImage profileImage;

}
