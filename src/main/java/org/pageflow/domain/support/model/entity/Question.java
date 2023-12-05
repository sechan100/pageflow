package org.pageflow.domain.support.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.user.entity.Profile;

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
public class Question extends BaseEntity {
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Profile questioner;
    
    @OneToOne(fetch = FetchType.LAZY)
    private Answer answer;
}

