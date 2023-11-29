package org.pageflow.domain.interaction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "preference",
        indexes = @Index(name = "idx_preference_1", columnList = "interactor_id, targetType, targetId", unique = true),
        uniqueConstraints = @UniqueConstraint(name = "uk_preference_1", columnNames = {"interactor_id", "targetType", "targetId"})
)
public class Preference extends Interaction {

    private boolean isLiked;
    
}
















