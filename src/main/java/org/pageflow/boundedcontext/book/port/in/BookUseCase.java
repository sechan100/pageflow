package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.application.dto.BookDto;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.Title;

/**
 * @author : sechan
 */
public interface BookUseCase {
    BookDto.Simple createBook(CreateBookCmd cmd);
    BookDto.Simple changeBookTitle(BookId id, Title title);
    BookDto.Simple changeBookCoverImage(BookId id, CoverImageUrl url);

}
