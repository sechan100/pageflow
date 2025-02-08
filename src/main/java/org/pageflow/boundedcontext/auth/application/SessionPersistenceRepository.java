package org.pageflow.boundedcontext.auth.application;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.auth.domain.SessionManager;
import org.pageflow.boundedcontext.auth.domain.token.RefreshToken;
import org.pageflow.boundedcontext.auth.domain.token.RefreshTokenIssuer;
import org.pageflow.boundedcontext.auth.persistence.SessionRepository;
import org.pageflow.boundedcontext.auth.port.out.SessionPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.AccountJpaRepository;
import org.pageflow.shared.annotation.PersistenceAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * @author : sechan
 */
@PersistenceAdapter
@Transactional
@RequiredArgsConstructor
public class SessionPersistenceRepository implements SessionPersistencePort {
  private final SessionRepository sessionJpaRepo;
  private final AccountJpaRepository accountJpaRepo;


  @Override
  public void save(SessionManager session) {
    sessionJpaRepo.persist(toEntity(session));
  }

  @Override
  public void delete(SessionId sessionId) {
    sessionJpaRepo.deleteById(sessionId.toLong());
  }

  @Override
  public void delete(SessionManager session) {
    delete(session.getId());
  }

  @Override
  public Optional<SessionManager> load(SessionId sid) {
    Optional<Session> entity = sessionJpaRepo.findById(sid.toLong());
    return entity.map(this::toSession);
  }


  private Session toEntity(SessionManager session) {
    return Session.builder()
      .id(session.getId().toLong())
      .account(accountJpaRepo.getReferenceById(session.getUid().toLong()))
      .role(session.getRole())
      .refreshToken(new RefreshToken(
        session.getRefreshTokenIssuer().getExp(),
        session.getRefreshTokenIssuer().getIat())
      )
      .build();
  }

  private SessionManager toSession(Session entity) {
    return new SessionManager(
      SessionId.from(entity.getId()),
      UID.from(entity.getAccount().getId()),
      entity.getRole(),
      new RefreshTokenIssuer(
        Instant.ofEpochMilli(entity.getRefreshToken().getExp()),
        Instant.ofEpochMilli(entity.getRefreshToken().getIat())
      )
    );
  }
}
