package org.pageflow.domain.book.model.outline;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PageSummaryWithChapterId {
    
    private Long id;
    private String title;
    private Integer orderNum;
    private Long chapterId;
    
}
