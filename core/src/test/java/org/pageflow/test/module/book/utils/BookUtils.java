package org.pageflow.test.module.book.utils;

import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.user.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Component
@Transactional
@RequiredArgsConstructor
public class BookUtils {
  private final BookUseCase bookUseCase;

  public BookDto createBook(UserDto user, String title) {
    return bookUseCase.createBook(user.getUid(), title, null).getSuccessData();
  }
}
