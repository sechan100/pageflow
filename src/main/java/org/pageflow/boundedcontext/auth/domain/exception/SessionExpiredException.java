package org.pageflow.boundedcontext.auth.domain.exception;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.common.exception.DomainException;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class SessionExpiredException extends DomainException {
    private final SessionId sessionId;
}
