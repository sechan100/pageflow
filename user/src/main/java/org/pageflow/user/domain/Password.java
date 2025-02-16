package org.pageflow.user.domain;


import org.pageflow.common.shared.type.SingleValueWrapper;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.user.application.config.PasswordEncoderConfig;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : sechan
 */
public class Password extends SingleValueWrapper<String> {
  private static final PasswordEncoder ENCODER = PasswordEncoderConfig.PASSWORD_ENCODER;
  private static final int MIN_LENGTH = 5;
  private static final int MAX_LENGTH = 36;
  private static final String REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])([A-Za-z0-9~!@#$%^&*()+_|=]|-){" + MIN_LENGTH + "," + MAX_LENGTH + "}$";

  private Password(String value) {
    super(value);
  }

  public static Password encrypt(String rawPassword) {
    FieldValidationResult result = validate(rawPassword);
    if(result.isValid()){
      String encrypted = ENCODER.encode(rawPassword);
      return new Password(encrypted);
    } else {
      throw new FieldValidationException(result);
    }
  }

  private static FieldValidationResult validate(String password) {
    assert password != null;

    FieldValidator<String> validator = new FieldValidator<>("password", password)
      .minLength(MIN_LENGTH, "비밀번호는 최소 " + MIN_LENGTH + "자 이상이어야 합니다.")
      .maxLength(MAX_LENGTH, "비밀번호는 최대 " + MAX_LENGTH + "자 이하여야 합니다.")
      .regex(REGEX, "비밀번호는 영문, 숫자, 특수문자를 조합하여 입력해주세요.");

    return validator.validate();
  }


}
