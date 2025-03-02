package org.pageflow.book.port.in;

import org.pageflow.book.port.in.token.BookPermission;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * 단일 책 리소스에 접근하기 위한 {@link BookPermission} 객체를 생성하는 팩토리 클래스
 * @author : sechan
 */
public interface BookAccessPermitter {
/**
   * 사용자가 책의 작가인지 즉, 소유자인지 판단하여 걸맞는 허가를 발행한다.
   * @param bookId
   * @param uid
   * @return
   */
  BookPermission grantIfOwner(UUID bookId, UID uid);
}
