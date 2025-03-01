package org.pageflow.book.port.in;

import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.port.in.token.BookContext;
import org.pageflow.common.user.UID;

import java.util.List;

/**
 * @author : sechan
 */
public interface BookShelfUseCase {
  /**
   * 책장에 책을 추가한다.
   * @param ctx
   * @param shlefOwnerId
   * @return
   */
  BookDto addBookToShelf(BookContext ctx, UID shlefOwnerId);

  /**
   * 책장에서 책을 제거한다.
   * @param ctx
   * @param shlefOwnerId
   * @return
   */
  BookDto removeBookFromShelf(BookContext ctx, UID shlefOwnerId);

  /**
   * 책장에 있는 책들을 조회한다.
   * @param shlefOwnerId
   * @return
   */
  List<BookDtoWithAuthor> getShelfBooks(UID shlefOwnerId);
}
