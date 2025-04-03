package org.pageflow.book.domain;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookAction;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.common.permission.ResourceAction;
import org.pageflow.common.permission.ResourcePermission;

import java.util.*;

/**
 * Book에 접근하여 데이터를 쓰거나 읽을 수 있는 권한을 나타내는 토큰
 *
 * @author : sechan
 */
public class BookPermission implements ResourcePermission<UUID> {
  private final UUID bookId;
  private final Set<BookAction> permittedActions;

  private BookPermission(UUID bookId, Collection<BookAction> permittedActions) {
    this.bookId = bookId;
    this.permittedActions = new HashSet<>(permittedActions);
  }

  /**
   * 작가 권한으로 허가를 발행
   * 어지간하면 {@link BookStatus#values()}를 사용하지 말 것; 새로운 권한을 추가했을 때, 원치 않는 동작이 발생할 수 있음.
   */
  public static BookPermission author(Book readOnlyBook) {
    BookStatus status = readOnlyBook.getStatus();
    List<BookAction> allow = new ArrayList<>(4);

    switch(status) {
      case DRAFT, REVISING -> {
        allow.add(BookAction.READ);
        allow.add(BookAction.EDIT);
        allow.add(BookAction.UPDATE_STATUS);
        allow.add(BookAction.DELETE);
      }
      case PUBLISHED -> {
        allow.add(BookAction.READ);
        allow.add(BookAction.UPDATE_STATUS);
        allow.add(BookAction.DELETE);
      }
    }
    return new BookPermission(readOnlyBook.getId(), allow);
  }

  /**
   * 독자 권한으로 허가를 발행
   * 어지간하면 {@link BookStatus#values()}를 사용하지 말 것; 새로운 권한을 추가했을 때, 원치 않는 동작이 발생할 수 있음.
   */
  public static BookPermission reader(Book readOnlyBook) {
    List<BookAction> allow = new ArrayList<>(2);
    BookStatus status = readOnlyBook.getStatus();
    BookVisibility visibility = readOnlyBook.getVisibility();

    // 출판된 책이 아니면 독자는 읽지 못함.
    if(status == BookStatus.PUBLISHED) {
      // 책이 PUBLIC이면 읽을 수 있음
      if(visibility == BookVisibility.PUBLIC) {
        allow.add(BookAction.READ);
      }
    }
    return new BookPermission(readOnlyBook.getId(), allow);
  }

  @Override
  public UUID getResourceId() {
    return this.bookId;
  }

  @Override
  public Set<? extends ResourceAction> getPermittedActions() {
    return permittedActions;
  }

  @Override
  public boolean isFullActionPermitted() {
    return permittedActions.containsAll(Arrays.asList(BookAction.values()));
  }
}
