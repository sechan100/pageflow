package org.pageflow.book.application;

import lombok.Getter;
import org.pageflow.common.result.MessageData;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@Getter
public enum BookCode implements ResultCode {
  EXTERNAL_COVER_IMAGE_URL("외부 커버 이미지 url은 사용할 수 없습니다."),
  TOC_HIERARCHY_ERROR("책 목차의 계층구조를 위반하는 요청입니다.", MessageData.class),
  INVALID_BOOK_STATUS("책이 요청을 처리하기 위해 요구되는 상태가 아닙니다.", BookStatusData.class),
  SECTION_HTML_CONTENT_PARSE_ERROR("섹션 HTML 컨텐츠 파싱에 실패했습니다.", Exception.class);

  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
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
