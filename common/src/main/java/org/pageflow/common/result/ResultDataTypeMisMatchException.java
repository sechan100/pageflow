package org.pageflow.common.result;

import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
public class ResultDataTypeMisMatchException extends RuntimeException {
  private final ResultCode code;
  private final Class<?> expectedType;
  private final Class<?> actualType;

  public ResultDataTypeMisMatchException(ResultCode code, Class<?> expected, Class<?> actual) {
    super(String.format("'%s' 타입은 ResultCode[%s]에서 정의된 data 타입인 '%s'가 아닙니다.", actual.getName(), code.name(), expected.getName()));
    this.code = code;
    this.expectedType = expected;
    this.actualType = actual;
  }
}
