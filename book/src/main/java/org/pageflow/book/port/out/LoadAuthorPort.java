package org.pageflow.book.port.out;

import org.pageflow.book.domain.Author;
import org.pageflow.common.user.UID;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface LoadAuthorPort {
  Optional<Author> loadAuthor(UID authorId);

  /**
   * jpa proxy로 load 해온다
   * @param authorId
   * @return
   */
  Author loadAuthorReference(UID authorId);
}
