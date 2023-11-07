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
public class ChapterSummary {
    
    private Long id;
    private String title;
    private Integer orderNum;
    private List<PageSummary> pages;
    
    
    public ChapterSummary(Chapter chapter, List<PageSummary> pages) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.orderNum = chapter.getOrderNum();
        this.pages = pages;
    }
    
}
