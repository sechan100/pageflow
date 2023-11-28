package org.pageflow.domain.interaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.domain.interaction.entity.Comment;

/**
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentWithPreference {
    private Comment comment;
    private PreferenceStatistics preferenceStatistics;
}
