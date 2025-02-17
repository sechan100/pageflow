package org.pageflow.common.result;

import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.code.CommonCode;
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
public class Result<T> {
  @Getter
  private final ResultCode code;
  private final T data;

  private Result(ResultCode code, T data) {
    matchCodeDataWithActualData(code, data);
    this.code = code;
    this.data = data;
  }

  private static <T> void matchCodeDataWithActualData(ResultCode code, T data) {
    Class<?> expectedDataType = Objects.requireNonNullElse(code.getDataType(), NullDataType.class);
    Class<?> actualDataType = Objects.requireNonNullElse(data, new NullDataType()).getClass();
    if(!expectedDataType.isAssignableFrom(actualDataType)){
      throw new ResultDataTypeMisMatchException(code, expectedDataType, actualDataType);
    }
  }

  public static Result of(ResultCode code) {
    return new Result(code, null);
  }

  public static <T> Result of(ResultCode code, T data) {
    return new Result(code, data);
  }

  public static <T> Result<T> success(T data) {
    return new Result<>(CommonCode.SUCCESS, data);
  }

  public boolean is(ResultCode code) {
    return this.code.equals(code);
  }

  public boolean isSuccess() {
    return is(CommonCode.SUCCESS);
  }

  /**
   * Result에 결과로 예측되는 code에 맞는 데이터를 반환한다.
   * 만약 예측한 code의 데이터 타입과 실제 담겨있는 데이터의 타입이 다르다면 {@link ResultDataTypeMisMatchException}을 발생시킨다.
   * @param expectedCode
   * @return
   */
  public T getData(ResultCode expectedCode) {
    matchCodeDataWithActualData(expectedCode, data);
    return data;
  }

  public T getSuccessData() {
    return getData(CommonCode.SUCCESS);
  }

  /**
   * ResultCode와 관계없이 그냥 담긴 데이터를 뽑아낸다.
   * @return
   */
  public T dangerouslyGetData() {
    return data;
  }
}


// ResultCode의 dataType이 null인 경우를 표현하기 위한 타입 객체
class NullDataType {
}
