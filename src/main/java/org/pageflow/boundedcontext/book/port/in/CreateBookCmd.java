package org.pageflow.boundedcontext.book.port.in;

import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * @author : sechan
 */
@Getter
public class CreateBookCmd {
    private final UID authorId;
    private final Title title;
    private final CoverImageUrl coverImageUrl;

    public CreateBookCmd(UID authorId, String title, String coverImageUrl) {
        this.authorId = authorId;
        this.title = Title.from(title);
        this.coverImageUrl = CoverImageUrl.from(coverImageUrl);
    }
}
