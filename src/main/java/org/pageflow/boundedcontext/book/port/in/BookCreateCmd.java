package org.pageflow.boundedcontext.book.port.in;

import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
@Getter
public class BookCreateCmd {
    private final UID authorId;
    private final Title title;
    private final CoverImageUrl coverImageUrl;

    public BookCreateCmd(TSID authorId, String title, String coverImageUrl) {
        this.authorId = UID.from(authorId);
        this.title = Title.from(title);
        this.coverImageUrl = CoverImageUrl.from(coverImageUrl);
    }
}
