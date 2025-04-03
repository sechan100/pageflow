package org.pageflow.book.port.in;

import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookUseCase {
  Result<BookDto> createBook(
    UID authorId,
    BookTitle title,
    @Nullable MultipartFile coverImage
  );

  BookDtoWithAuthor queryBook(UUID bookId);

  MyBooks queryMyBooks(UID uid);

  Result<BookDto> changeBookTitle(UUID bookId, BookTitle title);

  Result<BookDto> changeBookCoverImage(UUID bookId, MultipartFile file);

  void deleteBook(UUID bookId);
}
