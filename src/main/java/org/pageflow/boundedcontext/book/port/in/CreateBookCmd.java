package org.pageflow.boundedcontext.book.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;

/**
 * @author : sechan
 */
@Value
public class CreateBookCmd {
    Title title;
    CoverImageUrl coverImageUrl;
}
