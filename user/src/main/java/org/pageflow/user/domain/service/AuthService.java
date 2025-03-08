package org.pageflow.user.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.user.UID;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.domain.entity.Session;
import org.pageflow.user.domain.token.AccessToken;
import org.pageflow.user.dto.token.AccessTokenDto;
import org.pageflow.user.dto.token.AuthTokens;
import org.pageflow.user.port.in.IssueSessionCmd;
import org.pageflow.user.port.in.SessionUseCase;
import org.pageflow.user.port.out.LoadAccountPort;
import org.pageflow.user.port.out.entity.SessionPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements SessionUseCase {

  private final SessionPersistencePort sessionPersistencePort;
  private final LoadAccountPort loadAccountPort;
  private final TokenProvider tokenProvider;

  /**
   * TODO: 같은 사용자에 대한 Session이 이미 존재하는 경우 적용할 정책을 고민해보자.
   */
  @Override
  public AuthTokens issueSession(IssueSessionCmd cmd) {
    UID accountId = cmd.getAuthenticatedAccountId();
    Account account = loadAccountPort.load(accountId).orElseThrow();
    Session session = Session.issue(account);
    sessionPersistencePort.persist(session);
    // 세션을 사용하여 최초의 accessToken을 발급
    AccessToken accessToken = tokenProvider.issueAccessToken(session);
    return AuthTokens.from(accessToken, session);
  }

  @Override
  public AccessTokenDto refresh(UUID sessionId) {
    /**
     * 세션조회 실패시, scheduling에 의해 삭제된 '만료세션'으로 간주.
     * TODO: 세션 삭제 scheduling 구현하기
     */
    Session session = sessionPersistencePort.findById(sessionId)
      .orElseThrow(() -> new ProcessResultException(UserCode.SESSION_EXPIRED));

    // 새 토큰을 발급
    AccessToken accessToken = tokenProvider.issueAccessToken(session);
    return AccessTokenDto.from(accessToken);
  }

  @Override
  public void logout(UUID sessionId) {
    int isDeleted = sessionPersistencePort.deleteSessionById(sessionId);
  }


}
