package org.pageflow.domain.book.model.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.model.summary.ChapterSummary;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class ChapterUpdateRequest extends RearrangeRequest {

    public ChapterUpdateRequest() {
        super("chapter");
    }

    public ChapterUpdateRequest(Chapter chapter) {
        super("chapter", chapter.getId(), chapter.getTitle(), chapter.getSortPriority());
    }

    public ChapterUpdateRequest(ChapterSummary chapter) {
        super("chapter", chapter.getId(), chapter.getTitle(), chapter.getSortPriority());
    }
}