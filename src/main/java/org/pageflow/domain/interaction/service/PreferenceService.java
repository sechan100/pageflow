package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.interaction.entity.Preference;
import org.pageflow.domain.interaction.model.PreferenceStatistics;
import org.pageflow.domain.interaction.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class PreferenceService {
    
    private final PreferenceRepository preferenceRepository;
    
    public PreferenceStatistics getPreferenceStatistics(String targetType, Long targetId) {
        List<Preference> preferences = preferenceRepository.findAllByTargetTypeAndTargetId(targetType, targetId);
        PreferenceStatistics preferenceStatistics = new PreferenceStatistics();
        preferenceStatistics.setTargetId(targetId);
        preferenceStatistics.setTargetType(targetType);
        preferences.forEach(
                preference -> {
                    if (preference.isLiked()) {
                        preferenceStatistics.getLikes().add(preference);
                    } else {
                        preferenceStatistics.getDislikes().add(preference);
                    }
                }
        );
        return preferenceStatistics;
    }
}
