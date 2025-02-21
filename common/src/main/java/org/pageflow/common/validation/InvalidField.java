package org.pageflow.common.validation;

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class InvalidField {
  private final String field;
  private final FieldReason reason;
  private final String message;
  @Nullable
  private final String value;


  @Builder
  public InvalidField(
    String field,
    FieldReason reason,
    @Nullable String message,
    @Nullable Object value
  ) {
    this.field = field;
    this.reason = reason;
    this.message = message != null ? message : reason.getDefaultMessage();
    this.value = value != null ? value.toString() : null;
  }

  public FieldValidationException toException() {
    return new FieldValidationException(this);
  }

}
