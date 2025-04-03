package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.pageflow.book.adapter.out.AuthorAcl;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookPermission;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.permission.ResourcePermissionContext;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 단일 책 리소스에 접근하기 위한 {@link BookPermission} 객체를 {@link org.pageflow.common.permission.ResourcePermissionContext}에 설정하는 역할을 한다.
 *
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookAccessPermitter {
  private final ResourcePermissionContext resourcePermissionContext;
  private final BookPersistencePort bookPersistencePort;
  private final AuthorAcl authorAcl;


  private BookPermission _grant(UUID bookId, UID uid) {
    Book book = bookPersistencePort.findById(bookId).get();
    Book readOnlyBook = Hibernate.unproxy(book, Book.class);
    Author author = authorAcl.loadAuthorProxy(uid);
    boolean isAuthor = book.getAuthor().getUid().equals(author.getUid());

    if(isAuthor) {
      return BookPermission.author(readOnlyBook);
    } else {
      return BookPermission.reader(readOnlyBook);
    }
  }

  /**
   * 사용자가 책의 작가인지 즉, 소유자인지 판단하여 걸맞는 허가를 발행한다.
   *
   * @param bookId
   * @param uid
   * @return
   */
  public void setPermission(UUID bookId, UID uid) {
    BookPermission permission = _grant(bookId, uid);
    resourcePermissionContext.addResourcePermission(permission);
  }
}
