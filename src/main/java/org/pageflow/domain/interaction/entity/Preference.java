package org.pageflow.domain.interaction.entity;

import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Preference extends Interaction {

    private boolean isLiked;
    
}
















