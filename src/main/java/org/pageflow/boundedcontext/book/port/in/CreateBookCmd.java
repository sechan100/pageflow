package org.pageflow.boundedcontext.book.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * @author : sechan
 */
@Value
public class CreateBookCmd {
    UID authorId;
    Title title;
    CoverImageUrl coverImageUrl;
}
