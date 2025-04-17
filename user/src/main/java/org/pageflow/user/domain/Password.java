package org.pageflow.user.domain;


import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.utility.SingleValueWrapper;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.user.application.config.PasswordEncoderConfig;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : sechan
 */
public class Password extends SingleValueWrapper<String> {
  public static final PasswordEncoder ENCODER = PasswordEncoderConfig.PASSWORD_ENCODER;
  public static final int MIN_LENGTH = 5;
  public static final int MAX_LENGTH = 36;
  public static final String REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])([A-Za-z0-9~!@#$%^&*()+_|=]|-){" + MIN_LENGTH + "," + MAX_LENGTH + "}$";
  public static final String REGEX_MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 조합하여 입력해주세요.";

  private Password(String value) {
    super(value);
  }

  /**
   * Password 객체를 암호화하여 반환
   *
   * @param rawPassword
   * @return Result
   * - FIELD_VALIDATION_ERROR : 비밀번호가 유효하지 않을 때
   */
  public static Result<Password> encrypt(String rawPassword) {
    FieldValidationResult result = validate(rawPassword);
    if(result.isValid()) {
      String encrypted = ENCODER.encode(rawPassword);
      return Result.ok(new Password(encrypted));
    } else {
      return Result.unit(CommonCode.FIELD_VALIDATION_ERROR, result);
    }
  }

  public static boolean matches(String rawPassword, String encryptedPassword) {
    return ENCODER.matches(rawPassword, encryptedPassword);
  }

  private static FieldValidationResult validate(String password) {
    assert password != null;

    FieldValidator<String> validator = new FieldValidator<>("password", password)
      .minLength(MIN_LENGTH, "비밀번호는 최소 " + MIN_LENGTH + "자 이상이어야 합니다.")
      .maxLength(MAX_LENGTH, "비밀번호는 최대 " + MAX_LENGTH + "자 이하여야 합니다.")
      .regex(REGEX, REGEX_MESSAGE);

    return validator.validate();
  }


}
