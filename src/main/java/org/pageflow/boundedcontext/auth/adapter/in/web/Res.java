package org.pageflow.boundedcontext.auth.adapter.in.web;

import lombok.Builder;
import lombok.Value;

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

}
