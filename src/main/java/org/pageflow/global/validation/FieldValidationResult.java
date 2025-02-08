package org.pageflow.global.validation;

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
