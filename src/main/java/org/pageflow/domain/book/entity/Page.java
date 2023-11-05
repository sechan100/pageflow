package org.pageflow.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"chapter_id", "orderNum"}))
public class Page extends BaseEntity {
    
    @ManyToOne
    @JsonIgnore // 양방향 객체 참조로 인한 직렬화 무한루프에 빠지는 것을 막는다.
    private Chapter chapter;
    
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    @Min(1)
    @Column(nullable = false)
    private Integer orderNum;
    
    
    
    
    
    /**
     * 설정한 orderNum이 없다면, 챕터의 페이지들의 제일 마지막 순서로 새로운 페이지의 순서를 자동으로 설정.
     */
    @PrePersist
    private void setOrderNum() {
        if (this.orderNum == null) {
            this.orderNum = this.chapter.getPages().size() + 1;
        }
    }
}
