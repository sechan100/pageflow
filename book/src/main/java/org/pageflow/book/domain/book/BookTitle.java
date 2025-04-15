package org.pageflow.book.domain.book;

import org.pageflow.common.utility.SingleValueWrapper;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;

/**
 * @author : sechan
 */
public class BookTitle extends SingleValueWrapper<String> {
  public static final int MIN_LENGTH = 1;
  public static final int MAX_LENGTH = 50;

  private BookTitle(String value) {
    super(value);
  }

  public static BookTitle create(String value) {
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
