package org.pageflow.book.domain.enums;

import org.pageflow.common.permission.ResourceAction;

/**
 * enum 이름 함부로 바꾸면 안됨. 어노테이션에서 문자열로 넣어서 타입 안정성이 보장되지 않음
 * @author : sechan
 */
public enum BookResourceAction implements ResourceAction {
  /**
   * 읽기, 책 조회 등
   */
  READ,

  /**
   * 쓰기, folder 이름 변경, section 내용 수정, 목차 수정 등.
   */
  EDIT,

  /**
   * book status, isPublic 등 책 필드 변경 가능
   * 책 삭제 가능
   */
  UPDATE_STATUS,

  /**
   * 책 삭제
   */
  DELETE
}
