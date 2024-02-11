package org.pageflow.domain.user.model.principal;

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
    private Long UID;
    
    
    public static SessionPrincipal anonymous() {
        return SessionPrincipal.builder()
                .UID(0L)
                .build();
    }
}
