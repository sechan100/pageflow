package org.pageflow.book.domain;

import org.pageflow.common.utility.SingleValueWrapper;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;

/**
 * @author : sechan
 */
public class BookTitle extends SingleValueWrapper<String> {
  private static final int MIN_LENGTH = 1;
  private static final int MAX_LENGTH = 100;

  private BookTitle(String value) {
    super(value);
  }

  public static BookTitle validOf(String value) {
    FieldValidationResult result = validate(value);
    if(result.isValid()) {
      return new BookTitle(value);
    } else {
      throw new FieldValidationException(result);
    }
  }

  public static FieldValidationResult validate(String value) {
    FieldValidator<String> validator = FieldValidator
      .of("bookTitle", value)
      .minLength(MIN_LENGTH)
      .maxLength(MAX_LENGTH);

    return validator.validate();
  }
}
