package org.pageflow.common.api;

import lombok.Getter;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.ResultCode;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class ApiResponse<T> {

  /**
   * {@link ResultCode}의 name()이다.
   */
  private final String code;
  private final String message;
  @Nullable
  private final T data;


  private ApiResponse(Result<T> result) {
    ResultCode code = result.getCode();
    this.code = code.getCode();
    this.message = code.getMessage();
    this.data = result.getData();
  }

  public static <T> ApiResponse<T> of(Result<T> result) {
    return new ApiResponse<>(result);
  }

}
