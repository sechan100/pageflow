package org.pageflow.boundedcontext.auth.port.out;

import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;

/**
 * @author : sechan
 */
public interface CmdSessionPort {
    void save(Session session);

    void delete(Session session);
    void delete(SessionId sessionId);
}
