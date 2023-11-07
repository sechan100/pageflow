package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.pageflow.domain.book.entity.Page;

@Data
@AllArgsConstructor
public class PageUpdateRequest {
    
    private Long id;
    
    private String title;
    
    private Integer orderNum;
    
    private String content;
    
    
    
    public PageUpdateRequest(Page page){
        this.id = page.getId();
        this.title = page.getTitle();
        this.orderNum = page.getOrderNum();
        this.content = page.getContent();
    }
}
