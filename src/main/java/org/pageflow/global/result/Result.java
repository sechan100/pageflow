package org.pageflow.global.result;

import lombok.Getter;
import org.pageflow.global.result.code.ResultCode;

import java.util.Objects;

/**
 * @author : sechan
 */
@Getter
public class Result<T> {
  private final ResultCode code;
  private final T data;

  private Result(ResultCode code, T data) {
    Class<?> expectedDataType = Objects.requireNonNullElse(code.getDataType(), NullDataType.class);
    Class<?> actualDataType = Objects.requireNonNullElse(data, new NullDataType()).getClass();
    if(!expectedDataType.isAssignableFrom(actualDataType)){
      throw new ResultDataTypeMisMatchException(code, expectedDataType, actualDataType);
    }
    this.code = code;
    this.data = data;
  }

  public static Result of(ResultCode code) {
    return new Result(code, null);
  }

 public static <T> Result of(ResultCode code, T data) {
    return new Result(code, data);
  }
}


// ResultCode의 dataType이 null인 경우를 표현하기 위한 타입 객체
class NullDataType {
}
