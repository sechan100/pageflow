package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @EntityGraph(attributePaths = {"account"})
    RefreshToken findWithAccountById(String id);
}
