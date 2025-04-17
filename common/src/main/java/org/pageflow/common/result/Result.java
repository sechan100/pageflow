package org.pageflow.common.result;

import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.result.code.ResultCode;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 애플리케이션 전반에 걸쳐서 사용하는 응답객체.
 * 기본적으로 Api응답은 모두 200을 반환하고, 내부에서 사용하는 추가적인 코드를 응답객체에 담아서 반환한다.
 * 만약 데이터와 함께 특별한 응답코드를 반환하고 싶다면 해당객체를 반환하면된다.
 * <p>
 * 해당 객체는 Service에서 반환할 수도 있고, Controller에서 반환할 수도 있다.
 * Controller에서 반환하는 경우, Result.code를 담은 {@link ApiResponse}를 반환한다.
 *
 * @author : sechan
 */
public class Result<S> {
  @Getter
  private final ResultCode code;
  private final Object data;

  private Result(ResultCode code, Object data) {
    matchCodeDataWithActualData(code, data);
    this.code = code;
    this.data = data;
  }

  /**
   * ResultCode와 실제 데이터의 타입이 일치하는지 확인한다.
   * dataType이 Object로 지정된 ResultCode의 경우 null 데이터도 가능하다.
   * (Object > null > 모든 데이터 타입)
   *
   * @param code
   * @param data
   * @param <T>
   */
  private static <T> void matchCodeDataWithActualData(ResultCode code, T data) {
    Class<?> expectedDataType = Objects.requireNonNullElse(code.getDataType(), NullData.class);
    Class<?> actualDataType = Objects.requireNonNullElse(data, NullData.getInstance()).getClass();
    if(!expectedDataType.isAssignableFrom(actualDataType)) {
      throw new ResultDataTypeMisMatchException(code, expectedDataType, actualDataType);
    }
  }

  public static Result unit(ResultCode code) {
    return new Result(code, null);
  }

  public static <T> Result unit(ResultCode code, T data) {
    return new Result(code, data);
  }

  public static <S> Result<S> ok(S data) {
    return new Result<>(CommonCode.SUCCESS, data);
  }

  public static <S> Result<S> ok() {
    return new Result<>(CommonCode.SUCCESS, null);
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static <S> Result<S> fromOptional(Optional<S> optional) {
    return optional.map(Result::ok).orElseGet(() -> unit(CommonCode.DATA_NOT_FOUND, ""));
  }

  public boolean is(ResultCode code) {
    return this.code.equals(code);
  }

  public boolean isSuccess() {
    return is(CommonCode.SUCCESS);
  }

  public boolean isFailure() {
    return !isSuccess();
  }

  public <T> Result<T> flatMap(Function<S, Result<T>> mapper) {
    if(isSuccess()) {
      return mapper.apply((S) data);
    }
    return (Result<T>) this;
  }

  public <T> Result<T> map(Function<S, T> mapper) {
    if(isSuccess()) {
      return new Result<>(code, mapper.apply((S) data));
    }
    return (Result<T>) this;
  }

  public <T> Result<T> map(T data) {
    return this.map(unused -> data);
  }

  /**
   * Result에 결과로 예측되는 code에 맞는 데이터를 반환한다.
   *
   * @param expectedCode
   * @return data
   * - null인 경우에도 그대로 반환하므로 NPE에 주의
   * @throws ResultDataTypeMisMatchException ResultCode의 dataType과 실제 데이터의 타입이 다를 경우 발생
   */
  @Nullable
  public <T> T getData(ResultCode expectedCode) {
    if(this.code != expectedCode) {
      throw new IllegalArgumentException("Result의 code가 예상되는 code와 다릅니다. expected: " + expectedCode + ", actual: " + this.code);
    }
    matchCodeDataWithActualData(expectedCode, data);
    return (T) data;
  }

  /**
   * @return
   * @throws ResultDataTypeMisMatchException ResultCode의 dataType과 실제 데이터의 타입이 다를 경우 발생
   */
  public S getSuccessData() {
    return getData(CommonCode.SUCCESS);
  }

  /**
   * ResultCode와 관계없이 그냥 담긴 데이터를 뽑아낸다.
   *
   * @return
   */
  public Object getRawDataWithoutCodeCheck() {
    return data;
  }
}

