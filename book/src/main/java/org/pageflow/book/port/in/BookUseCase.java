package org.pageflow.book.port.in;

import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.book.port.in.token.BookContext;
import org.pageflow.common.user.UID;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public interface BookUseCase {
  BookDto createBook(
    UID authorId,
    BookTitle title,
    @Nullable MultipartFile coverImage
  );

  BookDtoWithAuthor queryBook(BookContext context);
  MyBooks queryMyBooks(UID uid);

  BookDto changeBookTitle(BookContext context, BookTitle title);

  BookDto changeBookCoverImage(BookContext context, MultipartFile file);

  void deleteBook(BookContext context);
}
