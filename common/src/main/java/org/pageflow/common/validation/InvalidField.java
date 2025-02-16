package org.pageflow.common.validation;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
@Builder
@RequiredArgsConstructor
public class InvalidField {
  private final String field;
  private final FieldReason reason;
  private final String message;
  @Nullable
  private final String value;

}
