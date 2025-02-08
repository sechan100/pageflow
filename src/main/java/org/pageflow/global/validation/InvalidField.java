package org.pageflow.global.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public class InvalidField {
  private final String field;
  private final FieldReason reason;
  @Nullable
  private final String value;

}
