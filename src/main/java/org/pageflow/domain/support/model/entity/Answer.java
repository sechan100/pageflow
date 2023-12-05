package org.pageflow.domain.support.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;

/**
 * @author : sechan
 */
@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
@DynamicUpdate
public class Answer extends BaseEntity {
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
}


