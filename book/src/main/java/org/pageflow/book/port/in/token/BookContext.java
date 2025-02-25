package org.pageflow.book.port.in.token;

import lombok.Getter;

import java.util.UUID;

/**
 * 특정 책에대한 Command를 수행할 때, 필요한 정보들을 포함하는 dto
 * <p>
 *   생명주기: HTTP Request, 즉 Thread 단위
 *   책의 작가가 변경되는 일은 불가능하기 때문에, 요청의 당사자가 책에 대해서 어떤 권한을 가지는지 등의 정보는 요청중 변경될 가능성이 없음
 * </p>
 * @author : sechan
 */
public class BookContext {
  @Getter
  private final UUID bookId;

  @Getter
  private final BookPermission permission;

  private BookContext(
    UUID bookId,
    BookPermission permission
  ) {
    this.bookId = bookId;
    this.permission = permission;
  }

  public static BookContext of(UUID bookId, BookPermission permission) {
    return new BookContext(bookId, permission);
  }
}
