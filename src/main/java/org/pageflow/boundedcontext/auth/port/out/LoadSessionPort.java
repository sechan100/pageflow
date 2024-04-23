package org.pageflow.boundedcontext.auth.port.out;

import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadSessionPort {
    Optional<Session> load(SessionId sid);
}
