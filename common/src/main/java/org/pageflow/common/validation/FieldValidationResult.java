package org.pageflow.common.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : sechan
 */
public class FieldValidationResult {
  private final List<InvalidField> invalidFields;

  public FieldValidationResult() {
    this.invalidFields = new ArrayList<>();
  }

  public static FieldValidationResult combine(FieldValidationResult... results) {
    FieldValidationResult combinedResult = new FieldValidationResult();
    for(FieldValidationResult result : results) {
      combinedResult.invalidFields.addAll(result.invalidFields);
    }
    return combinedResult;
  }

  public void addInvalidField(InvalidField invalidField) {
    invalidFields.add(invalidField);
  }

  public List<InvalidField> getInvalidFields() {
    return invalidFields;
  }

  public boolean isValid() {
    return invalidFields.isEmpty();
  }
}
