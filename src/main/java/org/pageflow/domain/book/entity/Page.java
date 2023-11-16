package org.pageflow.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"chapter_id", "sortPriority"}))
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
     * chapterId의 마지막 2자리를 가중치로 더하는 이유는, 만약 다른 챕터에 있던 페이지가 다른 챕터로 이동했을 때, sortPriority가 겹치지 않도록 하기 위함.
     */
    @PrePersist
    private void autoSetSortPriority() {
        if (this.sortPriority == null) {
            String chapter_str = String.valueOf(this.chapter.getId());
            // chapterId의 10의자리수 + 1의자리수  ex) 1523 -> 23 / 5 -> 5
            int chapterIdTensAndOnesPlace = Integer.parseInt(chapter_str.substring((chapter_str.length() > 1 ? chapter_str.length() - 2 : 0)));

            List<Page> pages = this.chapter.getPages();

            // 마지막 페이지의 sortPriority OR 0
            int lastChapterSortPriority = !pages.isEmpty() ? pages.get(pages.size() - 1).getSortPriority() : 0;

            // 새로운 sortPriority는 "(마지막sortPriority) + (10000) + (chapterId의 10의자리수 + 1의자리수)"
            int newSortPriority = lastChapterSortPriority + 10000 + chapterIdTensAndOnesPlace;

            this.sortPriority = newSortPriority;
        }
    }
}
