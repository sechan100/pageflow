package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.application.dto.BookDto;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;

/**
 * @author : sechan
 */
public interface BookUseCase {
    BookDto.Basic createBook(CreateBookCmd cmd);
    BookDto.Basic changeBookTitle(BookId id, Title title);
    BookDto.Basic changeBookCoverImage(BookId id, CoverImageUrl url);

}
