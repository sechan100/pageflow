package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.entity.ProfileEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    boolean existsByPenname(String penname);

    @EntityGraph(attributePaths = {"account"})
    ProfileEntity findWithAccountById(Long id);

}
