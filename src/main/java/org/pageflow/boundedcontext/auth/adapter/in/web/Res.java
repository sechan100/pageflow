package org.pageflow.boundedcontext.auth.adapter.in.web;

import lombok.Builder;
import lombok.Value;
import org.pageflow.boundedcontext.user.application.dto.UserDto;

/**
 * @author : sechan
 */
abstract class Res {

    @Value
    @Builder
    static class AccessToken {
        String compact;
        long exp;
    }

    @Value
    @Builder
    static class SessionInfo {
        UserDto.Session user;
    }


}
