package org.pageflow.test.e2e.book.shared;

import lombok.RequiredArgsConstructor;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class BookCreator {
  private final BookUseCase bookUseCase;

  public BookDto createBook(UID uid, String title) {
    return bookUseCase.createBook(uid, BookTitle.of(title), null);
  }
}
