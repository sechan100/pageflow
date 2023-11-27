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
public class CommentSummary {
    
    private Long targetId;
    private String targetType;
    
    private Long id;
    private String content;
    private UserSession author;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    
    private PreferenceStatistics preferenceStatistics;
}
