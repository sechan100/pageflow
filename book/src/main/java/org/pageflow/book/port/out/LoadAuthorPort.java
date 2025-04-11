package org.pageflow.book.port.out;

import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.domain.Author;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
public interface LoadAuthorPort {
  /**
   * jpa proxy로 load 해온다
   *
   * @param authorId
   * @return
   */
  Author loadAuthorProxy(UID authorId);

  AuthorProfileDto loadAuthorProfile(UID authorId);
}
