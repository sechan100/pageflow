package org.pageflow.book.port.in;

import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.common.user.UID;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookUseCase {
  BookDto createBook(
    UID authorId,
    BookTitle title,
    @Nullable MultipartFile coverImage
  );

  BookDto changeBookTitle(UUID bookId, BookTitle title);

  BookDto changeBookCoverImage(UUID bookId, MultipartFile file);

}
