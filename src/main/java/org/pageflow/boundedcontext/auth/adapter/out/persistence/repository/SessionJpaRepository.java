package org.pageflow.boundedcontext.auth.adapter.out.persistence.repository;


import org.pageflow.boundedcontext.auth.adapter.out.persistence.entity.SessionJpaEntity;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 * @author : sechan
 */
public interface SessionJpaRepository extends BaseJpaRepository<SessionJpaEntity, Long> {
    @EntityGraph(attributePaths = {"account"})
    SessionJpaEntity findWithAccountById(Long id);
}
