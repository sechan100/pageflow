package org.pageflow.book.application;

import lombok.Getter;
import org.pageflow.book.domain.enums.BookPermissionPolicy;
import org.pageflow.common.result.MessageData;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@Getter
public enum BookCode implements ResultCode {
    EXTERNAL_COVER_IMAGE_URL("외부 커버 이미지 url은 사용할 수 없습니다.")
  , BOOK_ACCESS_DENIED("책에 대한 접근 권한이 없습니다.", BookPermissionPolicy.class)
  , TOC_HIERARCHY_VIOLATION("책 목차의 계층구조를 위반하는 요청입니다.", MessageData.class)
  , INVALID_BOOK_STATUS("책이 요청을 처리하기 위해 요구되는 상태가 아닙니다.", BookStatusData.class)

  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String description;
  private final Class<?> dataType;

  BookCode(String description) {
    this(description, null);
  }

  BookCode(String description, Class<?> dataType) {
    this.description = description;
    this.dataType = dataType;
  }
}
