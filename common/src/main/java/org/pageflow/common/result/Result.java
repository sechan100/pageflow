package org.pageflow.common.result;

import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.code.ResultCode;

import java.util.Objects;

/**
 * 애플리케이션 전반에 걸쳐서 사용하는 응답객체.
 * 기본적으로 Api응답은 모두 200을 반환하고, 내부에서 사용하는 추가적인 코드를 응답객체에 담아서 반환한다.
 * 만약 데이터와 함께 특별한 응답코드를 반환하고 싶다면 해당객체를 반환하면된다.
 *
 * 해당 객체는 Service에서 반환할 수도 있고, Controller에서 반환할 수도 있다.
 * Controller에서 반환하는 경우, Result.code를 담은 {@link ApiResponse}를 반환한다.
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
