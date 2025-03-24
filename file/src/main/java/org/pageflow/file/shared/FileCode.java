package org.pageflow.file.shared;

import lombok.Getter;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@Getter
public enum FileCode implements ResultCode {

  FAIL_TO_DELETE_FILE("파일을 삭제하는데 실패했습니다.", String.class), // file static path

  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String description;
  private final Class<?> dataType;

  FileCode(String description) {
    this(description, null);
  }

  FileCode(String description, Class<?> dataType) {
    this.description = description;
    this.dataType = dataType;
  }


}
