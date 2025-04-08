package org.pageflow.book.domain;


import org.pageflow.book.domain.config.TocNodeConfig;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.utility.SingleValueWrapper;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;

/**
 * @author : sechan
 */
public class NodeTitle extends SingleValueWrapper<String> {

  public static final int MIN_LENGTH = 1;
  public static final int MAX_LENGTH = 50;

  private NodeTitle(String value) {
    super(value);
  }

  /**
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public static Result<NodeTitle> create(String value) {
    FieldValidationResult result = _validate(value);
    if(result.isValid()) {
      return Result.success(new NodeTitle(value));
    } else {
      return Result.of(CommonCode.FIELD_VALIDATION_ERROR, result);
    }
  }

  /**
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  private static FieldValidationResult _validate(String value) {
    FieldValidator<String> validator = FieldValidator
      .of("title", value)
      .minLength(MIN_LENGTH)
      .maxLength(MAX_LENGTH)
      .notSame(TocNodeConfig.ROOT_FOLDER_TITLE, "해당 제목은 사용할 수 없습니다.");

    return validator.validate();
  }
}
