package org.pageflow.domain.interaction.repository;

import org.pageflow.domain.interaction.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : sechan
 */
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    
    List<Preference> findAllByTargetTypeAndTargetId(String targetType, Long targetId);
}
