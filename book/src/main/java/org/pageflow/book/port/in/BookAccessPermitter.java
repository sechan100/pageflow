package org.pageflow.book.port.in;

import org.pageflow.book.application.BookPermission;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * 단일 책 리소스에 접근하기 위한 {@link BookPermission} 객체를 {@link org.pageflow.common.permission.ResourcePermissionContext}에 설정하는 역할을 한다.
 *
 * @author : sechan
 */
public interface BookAccessPermitter {
  /**
   * 사용자가 책의 작가인지 즉, 소유자인지 판단하여 걸맞는 허가를 발행한다.
   *
   * @param bookId
   * @param uid
   * @return
   */
  void setPermission(UUID bookId, UID uid);
}
