package org.pageflow.boundedcontext.auth.domain.exception;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.common.exception.DomainException;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * 이메일 인증을 시도하기 전에, 먼저 이메일 인증 요청용 메일을 전송해야함을 알리는 예외
 *
 * @author : sechan
 */
@RequiredArgsConstructor
public class RequireSendVerificationEmailException extends DomainException {
  private final UID uid;
}
