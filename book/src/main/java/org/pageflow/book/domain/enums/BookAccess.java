package org.pageflow.book.domain.enums;

/**
 * @author : sechan
 */
public enum BookAccess {
  // 기본적인 읽기 권한.
  READ,

  // 책의 실질적인 내용을 수정할 수 있는 권한(제목, 목차, 내용 등)
  WRITE,

  // 책의 상태, 설명을 변경하거나 삭제 등을 가능하게하는 권한
  AUTHOR,
}
