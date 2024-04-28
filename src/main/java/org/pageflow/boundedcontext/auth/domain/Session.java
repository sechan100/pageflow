package org.pageflow.boundedcontext.auth.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.global.api.code.Code1;
import org.pageflow.shared.annotation.AggregateRoot;

/**
 * @author : sechan
 */
@AggregateRoot
public class Session {
    @Getter
    private final SessionId id;
    @Getter
    private final UID uid;
    @Getter
    private final RoleType role;
    @Getter
    private final RefreshToken refreshToken;


    public Session(SessionId id, UID uid, RoleType role, RefreshToken refreshToken) {
        this.id = id;
        this.uid = uid;
        this.role = role;
        this.refreshToken = refreshToken;
    }


    public static Session login(UID uid, RoleType role) {
        SessionId sid = SessionId.random();
        RefreshToken rt = RefreshToken.issue();
        Session session = new Session(sid, uid, role, rt);
        return session;
    }

    public AccessToken refresh() {
        if(refreshToken.isExpired()){
            throw Code1.SESSION_EXPIRED.fire();
        }

        return AccessToken.issue(
            this.id,
            uid,
            role
        );
    }

}
