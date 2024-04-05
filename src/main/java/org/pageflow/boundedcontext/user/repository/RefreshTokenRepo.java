package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    @EntityGraph(attributePaths = {"account"})
    RefreshToken findWithAccountById(Long id);
}
