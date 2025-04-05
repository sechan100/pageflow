package org.pageflow.common.validation;

import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author : sechan
 */
public class FieldValidator<T> {
  private final String fieldName;
  private final T value;
  private final List<Function<T, InvalidField>> validators;

  public FieldValidator(String fieldName, T value) {
    this.fieldName = fieldName;
    this.value = value;
    this.validators = new ArrayList<>();
  }

  public static <T> FieldValidator<T> of(String fieldName, T value) {
    return new FieldValidator<>(fieldName, value);
  }

  /**
   * rule이 참이되게 하는 validator를 추가하면, 검증한다.
   *
   * @param validator validator가 false를 반환하면 reason을 이유로하는 InvalidField가 만들어진다.
   * @return
   */
  public FieldValidator<T> rule(Function<T, Boolean> validator, FieldReason reason, @Nullable String message) {
    InvalidField invalidField = InvalidField.builder()
      .field(fieldName)
      .reason(reason)
      .value(value.toString())
      .message(message != null ? message : reason.getDefaultMessage())
      .build();

    validators.add(v -> validator.apply(v) ? null : invalidField);
    return this;
  }

  public FieldValidationResult validate() {
    FieldValidationResult result = new FieldValidationResult();
    for(Function<T, InvalidField> validator : validators) {
      InvalidField invalidField = validator.apply(value);
      if(invalidField != null) {
        result.addInvalidField(invalidField);
      }
    }
    return result;
  }

  public FieldValidator<T> minLength(int length, String message) {
    return rule(v -> v.toString().length() >= length, FieldReason.TOO_SHORT, message);
  }

  public FieldValidator<T> minLength(int length) {
    return minLength(length, null);
  }

  public FieldValidator<T> maxLength(int length, String message) {
    return rule(v -> v.toString().length() <= length, FieldReason.TOO_LONG, message);
  }

  public FieldValidator<T> maxLength(int length) {
    return maxLength(length, null);
  }

  public FieldValidator<T> notEmpty(String message) {
    return rule(v -> !v.toString().isEmpty(), FieldReason.EMPTY, message);
  }

  public FieldValidator<T> notEmpty() {
    return notEmpty(null);
  }

  public FieldValidator<T> notNull(String message) {
    return rule(v -> v != null, FieldReason.NULL, message);
  }

  public FieldValidator<T> notNull() {
    return notNull(null);
  }

  public FieldValidator<T> minValue(int value, String message) {
    return rule(v -> Integer.parseInt(v.toString()) >= value, FieldReason.TOO_SMALL, message);
  }

  public FieldValidator<T> minValue(int value) {
    return minValue(value, null);
  }

  public FieldValidator<T> maxValue(int value, String message) {
    return rule(v -> Integer.parseInt(v.toString()) <= value, FieldReason.TOO_LARGE, message);
  }

  public FieldValidator<T> maxValue(int value) {
    return maxValue(value, null);
  }

  public FieldValidator<T> regex(String regex, String message) {
    return rule(v -> v.toString().matches(regex), FieldReason.INVALID_FORMAT, message);
  }

  public FieldValidator<T> regex(String regex) {
    return regex(regex, null);
  }

  public FieldValidator<T> email(String message) {
    return rule(v -> v.toString().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"), FieldReason.INVALID_FORMAT, message);
  }

  public FieldValidator<T> email() {
    return email(null);
  }

  public FieldValidator<T> phone(String message) {
    return rule(v -> v.toString().matches("^\\d{2,3}-\\d{3,4}-\\d{4}$"), FieldReason.INVALID_FORMAT, message);
  }

  public FieldValidator<T> phone() {
    return phone(null);
  }

  public FieldValidator<T> contains(String value, String message) {
    return rule(v -> v.toString().contains(value), FieldReason.CONTAINS_INVALID_VALUE, message);
  }

  public FieldValidator<T> contains(String value) {
    return contains(value, null);
  }

  /**
   * @param words
   * @param message values 중 조건에 걸린 값을 제공한다.
   * @return
   */
  public FieldValidator<T> notContains(Collection<String> words, Function<String, String> message) {
    validators.add(v -> {
      for(String word : words) {
        if(v.toString().contains(word)) {
          return InvalidField.builder()
            .field(fieldName)
            .reason(FieldReason.CONTAINS_INVALID_VALUE)
            .value(v.toString())
            .message(message.apply(word))
            .build();
        }
      }
      return null;
    });
    return this;
  }

  public FieldValidator<T> notContains(Collection<String> values) {
    return notContains(values, null);
  }

  /**
   * @param words
   * @param message words 중 조건에 걸린 값을 제공한다.
   * @return
   */
  public FieldValidator<T> notSame(Collection<String> words, Function<String, String> message) {
    validators.add(v -> {
      for(String word : words) {
        if(v.toString().equals(word)) {
          return InvalidField.builder()
            .field(fieldName)
            .reason(FieldReason.INVALID_VALUE)
            .value(v.toString())
            .message(message.apply(word))
            .build();
        }
      }
      return null;
    });
    return this;
  }

  public FieldValidator<T> notSame(Collection<String> values) {
    return notSame(values, null);
  }

  /**
   * @param value   검증할 값이 value와 같으면 검증 실패
   * @param message 검증 실패 시 제공할 메시지
   * @return
   */
  public FieldValidator<T> notSame(String value, String message) {
    List<String> wordContainer = new ArrayList<>();
    wordContainer.add(value);
    return notSame(wordContainer, (word) -> message);
  }

}
