package org.pageflow.book.application;

import lombok.Getter;
import org.pageflow.book.domain.BookPermissionPolicy;
import org.pageflow.common.result.AdditionalMessage;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@Getter
public enum BookCode implements ResultCode {
    EXTERNAL_COVER_IMAGE_URL("외부 커버 이미지 url은 사용할 수 없습니다.")
  , BOOK_ACCESS_DENIED("책에 대한 접근 권한이 없습니다.", BookPermissionPolicy.class)
  , TOC_HIERARCHY_VIOLATION("책 목차의 계층구조를 위반하는 요청입니다.", AdditionalMessage.class)

  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String message;
  private final Class<?> dataType;

  BookCode(String message) {
    this(message, null);
  }

  BookCode(String message, Class<?> dataType) {
    this.message = message;
    this.dataType = dataType;
  }
}
