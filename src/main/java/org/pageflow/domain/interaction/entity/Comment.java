package org.pageflow.domain.interaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "comment", indexes = {})
public class Comment extends Interaction {
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
}
















