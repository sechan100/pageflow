package org.pageflow.boundedcontext.user.adapter.out.persistence.repository;

import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.ProfileJpaEntity;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface ProfileJpaRepository extends BaseJpaRepository<ProfileJpaEntity, Long> {

    boolean existsByPenname(String penname);

    @EntityGraph(attributePaths = {"account"})
    Optional<ProfileJpaEntity> findWithAccountById(Long id);

}
