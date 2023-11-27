package org.pageflow.domain.user.repository;

import org.pageflow.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author         : sechan
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
