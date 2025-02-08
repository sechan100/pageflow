package org.pageflow.boundedcontext.auth.domain.exception;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.common.exception.DomainException;

import java.util.UUID;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class EmailVerificationAuthCodeMisMatchException extends DomainException {
  private final UUID failedAuthCode;
}
