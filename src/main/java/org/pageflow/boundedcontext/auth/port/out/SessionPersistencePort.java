package org.pageflow.boundedcontext.auth.port.out;

import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface SessionPersistencePort {
    Optional<Session> load(SessionId sid);
    void save(Session session);
    void delete(Session session);
    void delete(SessionId sessionId);
}
