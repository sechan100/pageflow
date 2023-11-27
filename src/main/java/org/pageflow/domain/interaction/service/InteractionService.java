package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.interaction.model.Interactions;
import org.pageflow.domain.interaction.model.PreferenceStatistics;
import org.springframework.stereotype.Service;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class InteractionService {
    
    private final PreferenceService preferenceService;
    private final CommentService commentService;
    
    public Interactions getAllInteractions(String targetType, Long targetId) {
        PreferenceStatistics preferences = preferenceService.getPreferenceStatistics(targetType, targetId);
        
        
        return Interactions.builder()
                .targetId(targetId)
                .targetType(targetType)
                .preferenceStatistics(preferences)
                .commentSummaries(commentService.getCommentSummaries(targetType, targetId))
                .build();
    }
    
}
