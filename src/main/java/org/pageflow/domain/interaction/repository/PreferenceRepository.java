package org.pageflow.domain.interaction.repository;

import org.pageflow.domain.interaction.entity.Preference;
import org.pageflow.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : sechan
 */
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    
    List<Preference> findAllByTargetTypeAndTargetId(String targetType, Long targetId);
    
    void deleteAllByTargetTypeAndTargetId(String targetType, Long targetId);
    
    Preference findByInteractorAndTargetTypeAndTargetId(Profile interactor, String targetType, Long targetId);
    
    void deleteByInteractorAndTargetTypeAndTargetId(Profile interactor, String targetType, Long targetId);
}
