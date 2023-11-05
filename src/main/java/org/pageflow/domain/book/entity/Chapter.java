package org.pageflow.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)//접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "order_num"}))
public class Chapter extends BaseEntity {
    
    @ManyToOne
    @JsonIgnore // 양방향 객체 참조로 인한 직렬화 무한루프에 빠지는 것을 막는다.
    private Book book;

    private String title;
    
    @Min(1)
    @Column(nullable = false)
    private Integer orderNum;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "chapter")
    private List<Page> pages = new ArrayList<>();
    
    
    
    
    
    /**
     * 설정한 orderNum이 없다면, 책의 챕터들의 제일 마지막 순서로 새로운 챕터의 순서를 자동으로 설정.
     */
    @PrePersist
    private void setOrderNum() {
        if (this.orderNum == null) {
            this.orderNum = this.book.getChapters().size() + 1;
        }
    }
    
    
    
    
    
}
