package org.pageflow.domain.interaction.entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.user.entity.Profile;

/**
 * @author : sechan
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@SuperBuilder
@AllArgsConstructor
abstract public class Interaction extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Profile interactor;
    
    private String targetType;
    
    private Long targetId;
}
