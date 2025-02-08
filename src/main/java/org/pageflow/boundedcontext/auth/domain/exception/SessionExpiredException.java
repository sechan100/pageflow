package org.pageflow.boundedcontext.auth.domain.exception;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.common.exception.DomainException;

import java.util.UUID;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class SessionExpiredException extends DomainException {
  private final UUID sessionId;
}
