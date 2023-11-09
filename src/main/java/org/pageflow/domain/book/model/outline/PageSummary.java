package org.pageflow.domain.book.model.outline;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pageflow.domain.book.entity.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PageSummary implements Rearrangeable {
    
    private Long id;
    private String title;
    private Integer sortPriority;
    private Long ownerId; // chapter_id
    
    public PageSummary(Page chapter, List<PageSummary> pages) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.sortPriority = chapter.getSortPriority();
        this.ownerId = chapter.getChapter().getId();
    }
}
