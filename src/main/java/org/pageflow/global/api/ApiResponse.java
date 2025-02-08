package org.pageflow.global.api;

import lombok.Getter;
import org.pageflow.global.result.Result;
import org.pageflow.global.result.code.ResultCode;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class ApiResponse<T> {

  private final String title;
  private final int code;
  private final String message;
  @Nullable
  private final T data;


  private ApiResponse(Result<T> result) {
    ResultCode code = result.getCode();
    this.title = code.getTitle();
    this.code = code.getCode();
    this.message = code.getMessage();
    this.data = result.getData();
  }

  public static <T> ApiResponse<T> of(Result<T> result) {
    return new ApiResponse<>(result);
  }

}
