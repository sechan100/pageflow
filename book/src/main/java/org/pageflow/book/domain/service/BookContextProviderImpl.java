package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.adapter.out.AuthorAcl;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.port.in.BookContextProvider;
import org.pageflow.book.port.in.token.BookContext;
import org.pageflow.book.port.in.token.BookPermission;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookContextProviderImpl implements BookContextProvider {
  private final BookPersistencePort bookPersistencePort;
  private final AuthorAcl authorAcl;

  @Override
  public BookContext getAuthorContext(UUID bookId, UID uid) {
    Book book = bookPersistencePort.findById(bookId).get();
    Author author = authorAcl.loadAuthorReference(uid);
    BookPermission permission = BookPermission.authorPermission(book, author);

    return BookContext.of(bookId, permission);
  }
}
