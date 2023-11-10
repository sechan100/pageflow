package org.pageflow.domain.book.model.outline;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.domain.book.entity.Chapter;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChapterSummary implements Rearrangeable {
    
    private Long id;
    private String title;
    private Integer sortPriority;
    private Long ownerId; // book_id
    private List<PageSummary> pages;
    
    
    public ChapterSummary(Chapter chapter, @Nullable List<PageSummary> pages) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.sortPriority = chapter.getSortPriority();
        this.ownerId = chapter.getBook().getId();
        
        // null인 pages가 들어오면 빈배열로 초기화
        this.pages = pages != null ? pages : new ArrayList<>();
    }
    
}
