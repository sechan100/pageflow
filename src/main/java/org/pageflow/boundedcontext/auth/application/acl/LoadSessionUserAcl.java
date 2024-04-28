package org.pageflow.boundedcontext.auth.application.acl;

import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;

/**
 * @author : sechan
 */
public interface LoadSessionUserAcl {
    UserDto.Session loadSessionUser(UID uid);
}
