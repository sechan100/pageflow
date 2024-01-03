package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.entity.TokenSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface TokenSessionRepository extends JpaRepository<TokenSession, String> {
    @EntityGraph(attributePaths = {"account"})
    TokenSession findWithAccountById(String id);
}
