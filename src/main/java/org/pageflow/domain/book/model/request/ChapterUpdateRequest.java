package org.pageflow.domain.book.model.request;

import lombok.Data;
import org.pageflow.domain.book.entity.Chapter;

@Data
public class ChapterUpdateRequest {
    
    private Long id;
    
    private String title;
    
    private Integer orderNum;
    
    
    
    public ChapterUpdateRequest(Chapter chapter){
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.orderNum = chapter.getOrderNum();
    }
}
