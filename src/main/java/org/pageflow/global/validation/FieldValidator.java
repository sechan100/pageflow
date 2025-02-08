package org.pageflow.global.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author : sechan
 */
public class FieldValidator<T> {
  private final String fieldName;
  private final T value;
  private final List<Function<T, FieldReason>> validators;

  public FieldValidator(String fieldName, T value) {
    this.fieldName = fieldName;
    this.value = value;
    this.validators = new ArrayList<>();
  }

  public static <T> FieldValidator<T> of(String fieldName, T value) {
    return new FieldValidator<>(fieldName, value);
  }

  /**
   * @param validator validator가 false를 반환하면 reason을 이유로하는 InvalidField가 만들어진다.
   * @return
   */
  public FieldValidator<T> rule(Function<T, Boolean> validator, FieldReason reason) {
    validators.add(v -> validator.apply(v) ? null : reason);
    return this;
  }

  public FieldValidationResult validate() {
    FieldValidationResult result = new FieldValidationResult();
    for(Function<T, FieldReason> validator : validators) {
      FieldReason reason = validator.apply(value);
      if(reason != null) {
        result.addInvalidField(this.invalid(reason));
      }
    }
    return result;
  }

  public FieldValidator<T> minLength(int length) {
    return rule(v -> v.toString().length() >= length, FieldReason.TOO_SHORT);
  }

  public FieldValidator<T> maxLength(int length) {
    return rule(v -> v.toString().length() <= length, FieldReason.TOO_LONG);
  }

  public FieldValidator<T> notEmpty() {
    return rule(v -> !v.toString().isEmpty(), FieldReason.EMPTY_STRING);
  }

  public FieldValidator<T> notNull() {
    return rule(v -> v != null, FieldReason.NULL);
  }

  public FieldValidator<T> minValue(int value) {
    return rule(v -> Integer.parseInt(v.toString()) >= value, FieldReason.TOO_SMALL);
  }

  public FieldValidator<T> maxValue(int value) {
    return rule(v -> Integer.parseInt(v.toString()) <= value, FieldReason.TOO_LARGE);
  }

  public FieldValidator<T> regex(String regex) {
    return rule(v -> v.toString().matches(regex), FieldReason.INVALID_FORMAT);
  }

  private InvalidField invalid(FieldReason reason) {
    return new InvalidField(fieldName, reason, value.toString());
  }

}
