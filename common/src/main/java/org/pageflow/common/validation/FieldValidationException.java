package org.pageflow.common.validation;

import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class FieldValidationException extends RuntimeException {
  private final FieldValidationResult result;

  public FieldValidationException(FieldValidationResult result) {
    super("Field validation failed");
    this.result = result;
  }

  public FieldValidationException(InvalidField invalidField) {
    super("Field validation failed");
    var result = new FieldValidationResult();
    result.addInvalidField(invalidField);
    this.result = result;
  }


}
