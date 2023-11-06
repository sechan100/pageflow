package org.pageflow.domain.book.model.outline;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PageSummary {
    
    private Long id;
    private String title;
    private Integer orderNum;
    
    
    
    
    public PageSummary(PageSummaryWithChapterId pageSummaryWithChapterId) {
        this.id = pageSummaryWithChapterId.getId();
        this.title = pageSummaryWithChapterId.getTitle();
        this.orderNum = pageSummaryWithChapterId.getOrderNum();
    }
}
