package org.pageflow.boundedcontext.user.model.user;

import lombok.Builder;
import lombok.Data;
import org.pageflow.boundedcontext.user.constants.RoleType;

/**
 * @author : sechan
 */
@Data
@Builder
public class SessionUser {
    private Long uid;
    private String username;
    private String email;
    private String penname;
    private String profileImgUrl;
    private RoleType role;
    private boolean isEmailVerified;
}
