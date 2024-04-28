package org.pageflow.boundedcontext.auth.adapter.out.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.adapter.out.persistence.entity.RefreshTokenJpaEmbedded;
import org.pageflow.boundedcontext.auth.adapter.out.persistence.entity.SessionJpaEntity;
import org.pageflow.boundedcontext.auth.domain.RefreshToken;
import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.auth.port.out.SessionPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.repository.AccountJpaRepository;
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
    private final SessionJpaRepository sessionJpaRepo;
    private final AccountJpaRepository accountJpaRepo;


    @Override
    public void save(Session session) {
        sessionJpaRepo.persist(toEntity(session));
    }

    @Override
    public void delete(SessionId sessionId) {
        sessionJpaRepo.deleteById(sessionId.toLong());
    }

    @Override
    public void delete(Session session) {
        delete(session.getId());
    }

    @Override
    public Optional<Session> load(SessionId sid) {
        Optional<SessionJpaEntity> entity =  sessionJpaRepo.findById(sid.toLong());
        return entity.map(this::toSession);
    }


    private SessionJpaEntity toEntity(Session session){
        return SessionJpaEntity.builder()
            .id(session.getId().toLong())
            .account(accountJpaRepo.getReferenceById(session.getUid().toLong()))
            .role(session.getRole())
            .refreshToken(new RefreshTokenJpaEmbedded(
                session.getRefreshToken().getExp(),
                session.getRefreshToken().getIat())
            )
            .build();
    }

    private Session toSession(SessionJpaEntity entity){
        return new Session(
            SessionId.from(entity.getId()),
            UID.from(entity.getAccount().getId()),
            entity.getRole(),
            new RefreshToken(
                Instant.ofEpochMilli(entity.getRefreshToken().getExp()),
                Instant.ofEpochMilli(entity.getRefreshToken().getIat())
            )
        );
    }
}
