package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.model.outline.ChapterSummary;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ChapterUpdateRequest extends RearrangeRequest {


    
    public ChapterUpdateRequest(Chapter chapter) {
        super("chapter", chapter.getId(), chapter.getTitle(), chapter.getSortPriority());
    }
    
    public ChapterUpdateRequest(ChapterSummary chapter) {
        super("chapter", chapter.getId(), chapter.getTitle(), chapter.getSortPriority());
    }
}
