package org.pageflow.domain.book.model.outline;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.domain.book.entity.Chapter;

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
    
    
    public ChapterSummary(Chapter chapter, List<PageSummary> pages) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.sortPriority = chapter.getSortPriority();
        this.ownerId = chapter.getBook().getId();
        this.pages = pages;
    }
    
}
