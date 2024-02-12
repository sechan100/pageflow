package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author         : sechan
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    boolean existsByPenname(String penname);
    
    @EntityGraph(attributePaths = {"account"})
    Profile findWithAccountById(Long UID);
    
}
