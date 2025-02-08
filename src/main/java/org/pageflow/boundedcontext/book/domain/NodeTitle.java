package org.pageflow.boundedcontext.book.domain;

import org.pageflow.boundedcontext.common.exception.FieldValidationException;
import org.pageflow.global.validation.FieldValidationResult;
import org.pageflow.global.validation.FieldValidator;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public class NodeTitle extends SingleValueWrapper<String> {

  private static final int MIN_LENGTH = 1;
  private static final int MAX_LENGTH = 100;

  public NodeTitle(String value) {
    super(value);
  }

  public static NodeTitle validOf(String value) {
    FieldValidationResult result = validate(value);
    if(result.isValid()) {
      return new NodeTitle(value);
    } else {
      throw new FieldValidationException(result);
    }
  }

  public static FieldValidationResult validate(String value) {
    FieldValidator<String> validator = FieldValidator
    .of("nodeTitle", value)
    .minLength(MIN_LENGTH)
    .maxLength(MAX_LENGTH);

    return validator.validate();
  }


}
