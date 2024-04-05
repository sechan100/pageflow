package org.pageflow.boundedcontext.user.model.principal;

import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 이미 존재하는 세션에 AccessToken으로 인증한 사용자의 principal 타입으로 사용됨.
 * @author : sechan
 */
@Getter
@Builder
@AllArgsConstructor
public class SessionPrincipal implements PageflowPrincipal {
    private TSID UID;

    public static SessionPrincipal anonymous() {
        return SessionPrincipal.builder()
                .UID(TSID.Factory.getTsid())
                .build();
    }
}
