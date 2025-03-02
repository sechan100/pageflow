package org.pageflow.book.application;

import org.pageflow.book.domain.enums.BookAction;
import org.pageflow.common.permission.ResourceAction;
import org.pageflow.common.permission.ResourcePermission;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * Book에 접근하여 데이터를 쓰거나 읽을 수 있는 권한을 나타내는 토큰
 * @author : sechan
 */
public class BookPermission implements ResourcePermission<UUID> {
  private final UUID bookId;
  private final Set<BookAction> permittedActions;

  private BookPermission(UUID bookId, BookAction... permittedActions) {
    this.bookId = bookId;
    this.permittedActions = Set.of(permittedActions);
  }

  /**
   * 작가 권한으로 허가를 발행
   * @param book
   * @param author
   * @return
   */
  public static BookPermission author(UUID bookId) {
    return new BookPermission(bookId, BookAction.values());
  }

  public static BookPermission reader(UUID bookId) {
    return new BookPermission(bookId, BookAction.READ);
  }

  public static BookPermission denied(UUID bookId) {
    return new BookPermission(bookId);
  }

  @Override
  public UUID getResourceId(){
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
