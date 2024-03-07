package org.pageflow.boundedcontext.user.repository;

import org.pageflow.boundedcontext.user.entity.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author         : sechan
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    boolean existsByPenname(String penname);
    
    @EntityGraph(attributePaths = {"account"})
    Profile findWithAccountByUID(Long UID);
    
}
