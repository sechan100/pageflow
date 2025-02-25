package org.pageflow.book.port.in.status;

import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.token.BookContext;

/**
 * {@link BookStatus#PUBLISHED} 상태인 책에서 사용가능한 행위들
 * @author : sechan
 */
public interface PublishedUseCase {
  /**
   * 책을 개정상태로 변경
   * @param context
   * @return
   */
  BookDto reviseBook(BookContext context);
}
