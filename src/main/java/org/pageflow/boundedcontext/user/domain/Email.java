package org.pageflow.boundedcontext.user.domain;

import org.pageflow.boundedcontext.common.exception.FieldValidationException;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.util.StringUtils;

/**
 * @author : sechan
 */
public class Email extends SingleValueWrapper<String> {

  private Email(String value) {
    super(value);
  }

  public static Email from(String value) {
    return new Email(value);
  }

  private static void validate(String email) {
    StringUtils.hasText(email);
    // 정규식
    if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
      throw FieldValidationException.builder()
        .message("올바른 이메일 형식이 아닙니다.")
        .field("email", email)
        .build();
    }
  }
}
