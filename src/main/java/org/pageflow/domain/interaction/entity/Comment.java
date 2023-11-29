package org.pageflow.domain.interaction.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.domain.interaction.model.InteractionPair;

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
@Table(name = "comment", indexes = {
        @Index(name = "idx_comment_target", columnList = "targetType, targetId")
})
public class Comment extends Interaction {
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Transient
    private InteractionPair pair;
    
    
    @PostLoad
    public void postLoadSetPair() {
        this.pair = new InteractionPair<>(super.getInteractor(), this);
    }
}
















