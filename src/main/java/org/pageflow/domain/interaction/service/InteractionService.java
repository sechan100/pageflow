package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.model.Interactions;
import org.springframework.stereotype.Service;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class InteractionService {
    
    private final PreferenceService preferenceService;
    private final CommentService commentService;
    
    public Interactions getAllInteractionsOfTarget(InteractionPair pair) {
        return Interactions.builder()
                .preferenceStatistics(preferenceService.getPreferenceStatistics(pair))
                .comments(commentService.getCommentsWithPreference(pair))
                .build();
    }
    
}
