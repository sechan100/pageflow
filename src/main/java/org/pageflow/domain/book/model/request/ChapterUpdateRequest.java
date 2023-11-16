package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.model.outline.ChapterSummary;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterUpdateRequest extends RearrangeRequest {

    private String title;


    public ChapterUpdateRequest(Chapter chapter) {
        super("chapter", chapter.getId(), chapter.getSortPriority());
        this.title = chapter.getTitle();
    }

    public ChapterUpdateRequest(ChapterSummary chapter) {
        super("chapter", chapter.getId(), chapter.getSortPriority());
        this.title = chapter.getTitle();
    }
}
