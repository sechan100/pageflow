package org.pageflow.boundedcontext.user.dto.user;

import lombok.Builder;
import lombok.Data;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
@Data
@Builder
public class SessionUser {
    private TSID uid;
    private String username;
    private String email;
    private String penname;
    private String profileImgUrl;
    private RoleType role;
    private boolean isEmailVerified;
}
