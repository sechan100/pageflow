package org.pageflow.domain.interaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.domain.user.model.dto.UserSession;

import java.time.LocalDateTime;

/**
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentWithPreference {
    
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private UserSession interactor;
    private String targetType;
    private Long targetId;
    private String content;
    private PreferenceStatistics preferenceStatistics;
}
