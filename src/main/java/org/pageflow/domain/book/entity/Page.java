package org.pageflow.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"chapter_id", "sortPriority"}))
@DynamicUpdate
public class Page extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore // 양방향 객체 참조로 인한 직렬화 무한루프에 빠지는 것을 막는다.
    private Chapter chapter;
    
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    @Min(1)
    @Column(nullable = false)
    private Integer sortPriority;
    
    
    
    
    
    /**
     * sortPriority 자동 설정
     * ex) 마지막 page의 sortPriority가 4710이라면, 새로운 page의 sortPriority는 5000으로 설정
     */
    @PrePersist
    private void autoSetSortPriority() {
        if (this.sortPriority == null) {
            
            Integer lastPageSortPriority = 1000;
            
            if(!this.chapter.getPages().isEmpty()) {
                lastPageSortPriority =this.chapter.getPages().get(this.chapter.getPages().size() - 1).getSortPriority();
                lastPageSortPriority = Integer.parseInt(lastPageSortPriority.toString().substring(0, 1)) * 1000 + 1000;
            }
            
            this.sortPriority = lastPageSortPriority;
        }
    }
}
