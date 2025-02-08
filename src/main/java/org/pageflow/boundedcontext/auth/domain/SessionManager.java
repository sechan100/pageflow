package org.pageflow.boundedcontext.auth.domain;

import org.pageflow.boundedcontext.auth.domain.exception.SessionExpiredException;
import org.pageflow.boundedcontext.auth.domain.token.AccessToken;
import org.pageflow.boundedcontext.auth.domain.token.RefreshToken;
import org.pageflow.boundedcontext.auth.persistence.SessionRepository;

import java.util.UUID;


/**
 * @author : sechan
 */
public class SessionManager {
  private final SessionRepository sessionRepository;
  private final


  public SessionManager(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }


  public SessionManager login(Account account) {
    UUID sessionId = UUID.randomUUID();
    RefreshToken refreshToken = RefreshToken.issue();
    Session session = new Session(
      sessionId,
      account,
      account.getRole(),
      refreshToken
    );
    return session;
  }

  public AccessToken refresh() {
    if(refreshTokenIssuer.isExpired()){
      throw new SessionExpiredException(id);
    }

    return AccessToken.issue(
      this.id,
      uid,
      role
    );
  }

}
