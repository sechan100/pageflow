package org.pageflow.common.result;

import lombok.Getter;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.code.ResultCode;

/**
 * 사실상 GOTO나 마찬가지다.
 *
 * 해당 예외는 (처리되지 않는 한) {@link org.pageflow.core.advice.ExceptionRestAdvice}에서 처리된다.
 * 사용자에게는 ProcessResultException.result의 값을 이용한 {@link ApiResponse}가 반환된다.
 * @author : sechan
 */
public class ProcessResultException extends RuntimeException {
  @Getter
  private final Result result;

  public ProcessResultException(Result result) {
    this(result, null);
  }

  public ProcessResultException(ResultCode code) {
    this(Result.of(code));
  }

  public ProcessResultException(Result result, Throwable cause) {
    super(result.getCode().getMessage(), cause);
    this.result = result;
  }
}
