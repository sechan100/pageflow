package org.pageflow.user.port.in;

import org.pageflow.user.dto.token.AccessTokenDto;
import org.pageflow.user.dto.token.AuthTokens;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SessionUseCase {
  /**
   * <p>세션을 발급한다. </p>
   * <p>
   *   사실상 login 처리를 하는 것이지만, 인증에 관련한 부분은 SpringSecurity에서 처리하고
   *   여기서는 실제 세션을 발급하는 일만 수행한다.
   * </p>
   * @param cmd
   * @return
   */
  AuthTokens issueSession(IssueSessionCmd cmd);

  AccessTokenDto refresh(UUID sessionId);
  void logout(UUID sessionId);
}