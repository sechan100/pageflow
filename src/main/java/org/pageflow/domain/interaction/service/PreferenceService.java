package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.interaction.entity.Preference;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.model.PreferenceStatistics;
import org.pageflow.domain.interaction.repository.PreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PreferenceService {
    
    private final PreferenceRepository preferenceRepository;
    
    public <T extends BaseEntity> PreferenceStatistics getPreferenceStatistics(T entity) {
        List<Preference> preferences = preferenceRepository.findAllByTargetTypeAndTargetId(entity.getClass().getSimpleName(), entity.getId());
        PreferenceStatistics preferenceStatistics = new PreferenceStatistics();
        preferences.forEach(
                preference -> {
                    if (preference.isLiked()) {
                        preferenceStatistics.addLike();
                    } else {
                        preferenceStatistics.addDislike();
                    }
                }
        );
        return preferenceStatistics;
    }
    
    
    // [CREATE] as like or dislike
    public Preference createPreference(InteractionPair pair, boolean isLiked) {
        Preference preference = Preference.builder()
                .interactor(pair.getInteractor())
                .targetType(pair.getTargetType())
                .targetId(pair.getTargetId())
                .isLiked(isLiked)
                .build();
        return preferenceRepository.save(preference);
    }
    
    
    // [UPDATE] like 또는 dislike로 변경
    public Preference updatePreferenceIsLiked(InteractionPair pair, boolean isLiked) {
        Preference preference = preferenceRepository.findByInteractorAndTargetTypeAndTargetId(pair.getInteractor(), pair.getTargetType(), pair.getTargetId());
        preference.setLiked(isLiked);
        return preferenceRepository.save(preference);
    }
    
    
    /**
     * 상호작용의 타겟에 딸려있는 모든 preference들을 삭제한다.
     * 주로 타겟 엔티티가 삭제된 경우, 참조를 잃은 preference들을 삭제하기 위해 사용.
     */
    // [DELETE] all
    public void deleteAllPreferences(InteractionPair pair) {
        preferenceRepository.deleteAllByTargetTypeAndTargetId(pair.getTargetType(), pair.getTargetId());
    }
    
    
    // [DELETE]
    public void deletePreference(InteractionPair pair) {
        preferenceRepository.deleteByInteractorAndTargetTypeAndTargetId(pair.getInteractor(), pair.getTargetType(), pair.getTargetId());
    }
    
    
    /**
     * [READ]
     * @param pair: 상호작용 쌍
     * @return 해당 상호작용 쌍을 이어주는 Preference 상호작용 객체가 존재한다면 반환, 없다면 null 반환
     */
    public Preference findPreferenceOrElseNull(InteractionPair pair) {
        return preferenceRepository.findByInteractorAndTargetTypeAndTargetId(pair.getInteractor(), pair.getTargetType(), pair.getTargetId());
    }
}
