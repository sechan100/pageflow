package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.adapter.out.AuthorAcl;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.permission.ResourcePermissionContext;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultBookAccessPermitter implements BookAccessPermitter {
  private final ResourcePermissionContext resourcePermissionContext;
  private final BookPersistencePort bookPersistencePort;
  private final AuthorAcl authorAcl;


  private BookPermission _grant(UUID bookId, UID uid) {
    Book book = bookPersistencePort.findById(bookId).get();
    Author author = authorAcl.loadAuthorProxy(uid);

    boolean isAuthor = book.getAuthor().getUid().equals(author.getUid());
    if(isAuthor) {
      return BookPermission.ofAuthor(bookId);
    } else {
      // 작가가 아니라면 책의 상태에 따라 권한을 부여한다.
      boolean isDraft = book.getStatus() == BookStatus.DRAFT;
      if(!isDraft) {
        return BookPermission.ofReader(bookId);
      } else {
        return BookPermission.ofDenied(bookId);
      }

    }
  }

  @Override
  public void setPermission(UUID bookId, UID uid) {
    BookPermission permission = _grant(bookId, uid);
    resourcePermissionContext.addResourcePermission(permission);
  }
}
