package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.summary.PageSummary;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageUpdateRequest extends RearrangeRequest {

    private String content;
    
    public PageUpdateRequest(Page page) {
        super("page", page.getId(), page.getTitle(), page.getSortPriority());
        this.content = page.getContent();
    }

    public PageUpdateRequest(PageSummary page) {
        super("page", page.getId(), page.getTitle(), page.getSortPriority());
    }
}