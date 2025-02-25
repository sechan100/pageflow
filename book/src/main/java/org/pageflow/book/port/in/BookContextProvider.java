package org.pageflow.book.port.in;

import org.pageflow.book.port.in.token.BookContext;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookContextProvider {
  /**
   * 작가 권한으로 context를 가져온다.
   * @param bookId
   * @param uid
   * @return
   */
  BookContext getAuthorContext(UUID bookId, UID uid);
}
