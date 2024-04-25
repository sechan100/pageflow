package org.pageflow.boundedcontext.auth.application.acl;

import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.UID;

/**
 * @author : sechan
 */
public interface LoadSessionUserAcl {
    UserDto.Session loadSessionUser(UID uid);
}
