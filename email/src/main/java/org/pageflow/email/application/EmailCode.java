package org.pageflow.email.application;

import lombok.Getter;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@Getter
public enum EmailCode implements ResultCode {
  FAIL_TO_SEND_MAIL("메일 전송에 실패하였습니다."),






  /********************************************************************************
   ********* 새로운 ResultCode를 만들 때, 해당 주석을 포함하여 아래부분을 붙여넣으면 된다. ********
   ********************************************************************************/
  ;
  private final String description;
  private final Class<?> dataType;

  EmailCode(String description) {
    this(description, null);
  }

  EmailCode(String description, Class<?> dataType) {
    this.description = description;
    this.dataType = dataType;
  }
}
