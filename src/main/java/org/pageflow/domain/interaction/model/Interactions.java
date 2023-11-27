package org.pageflow.domain.interaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : sechan
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Interactions {
    
    private Long targetId;
    private String targetType;
    
    // 댓글들
    private List<CommentSummary> commentSummaries;
    
    // 선호 통계
    private PreferenceStatistics preferenceStatistics;
    
}
