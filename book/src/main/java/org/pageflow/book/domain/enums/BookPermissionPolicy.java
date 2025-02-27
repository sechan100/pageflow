package org.pageflow.book.domain.enums;

/**
 * @author : sechan
 */
public enum BookPermissionPolicy {
  /**
   * 읽기, 책 조회 등
   */
    READ

  /**
   * 쓰기, folder 이름 변경, section 내용 수정, 목차 수정 등.
   */
  , WRITE

  /**
   * book status, isPublic 등 책 필드 변경 가능
   * 책 삭제 가능
   */
  , UPDATE_META_INFO

  // 전체
  , FULL
}
