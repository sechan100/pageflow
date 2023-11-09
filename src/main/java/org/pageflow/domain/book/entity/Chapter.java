package org.pageflow.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)//접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "sortPriority"}))
@DynamicUpdate
public class Chapter extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore // 양방향 객체 참조로 인한 직렬화 무한루프에 빠지는 것을 막는다.
    private Book book;

    private String title;
    
    @Min(1)
    @Column(nullable = false)
    private Integer sortPriority;
    
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "chapter")
    private List<Page> pages = new ArrayList<>();
    
    
    /**
     * sortPriority 자동 설정
     * ex) 마지막 chapter의 sortPriority가 4710이라면, 새로운 chapter의 sortPriority는 5000으로 설정
     */
    @PrePersist
    private void autoSetSortPriority() {
        if (this.sortPriority == null) {
            
            Integer lastChapterSortPriority = 1000;
            
            if(!this.book.getChapters().isEmpty()) {
                lastChapterSortPriority = this.book.getChapters().get(this.book.getChapters().size() - 1).getSortPriority();
                lastChapterSortPriority = Integer.parseInt(lastChapterSortPriority.toString().substring(0, 1)) * 1000 + 1000;
            }
            
            this.sortPriority = lastChapterSortPriority;
        }
    }
    
    
    // AllArgsConstructor: Pages 값을 초기화하기위해서 하드코딩
    public Chapter(Book book, String title, Integer sortPriority, List<Page> pages) {
        this.book = book;
        this.title = title;
        this.sortPriority = sortPriority;
        this.pages = Objects.requireNonNullElseGet(pages, ArrayList::new);
    }
}
