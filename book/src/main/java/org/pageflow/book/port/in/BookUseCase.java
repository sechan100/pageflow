package org.pageflow.book.port.in;

import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
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

  BookDtoWithAuthor queryBook(BookPermission permission);
  MyBooks queryMyBooks(UID uid);

  BookDto changeBookTitle(BookPermission permission, BookTitle title);

  BookDto changeBookCoverImage(BookPermission permission, MultipartFile file);

  void deleteBook(BookPermission permission);

}
