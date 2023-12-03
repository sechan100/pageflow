package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.interaction.model.InteractionsOfTarget;
import org.springframework.stereotype.Service;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class InteractionService {
    
    private final PreferenceService preferenceService;
    private final CommentService commentService;
    
    public <T extends BaseEntity> InteractionsOfTarget getAllInteractionsOfTarget(T entity) {
        return InteractionsOfTarget.builder()
                .preferenceStatistics(preferenceService.getPreferenceStatistics(entity))
                .comments(commentService.getCommentsWithPreference(entity))
                .build();
    }
    
}
