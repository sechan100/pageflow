package org.pageflow.boundedcontext.common.exception;

import lombok.Getter;
import org.pageflow.global.validation.FieldValidationResult;

/**
 * @author : sechan
 */
@Getter
public class FieldValidationException extends DomainException {
  private final FieldValidationResult result;

  public FieldValidationException(FieldValidationResult result) {
    super("Field validation failed");
    this.result = result;
  }


}
