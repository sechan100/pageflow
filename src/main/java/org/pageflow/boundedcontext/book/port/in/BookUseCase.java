package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.application.dto.BookDto;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.Title;

/**
 * @author : sechan
 */
public interface BookUseCase {
    BookDto.Book createBook(CreateBookCmd cmd);
    BookDto.Book changeBookTitle(BookId id, Title title);
    BookDto.Book changeBookCoverImage(BookId id, CoverImageFile file);

}
