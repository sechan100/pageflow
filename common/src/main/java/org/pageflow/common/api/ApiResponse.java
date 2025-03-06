package org.pageflow.common.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  private final String description;
  @Nullable
  private final T data;


  @JsonCreator
  public ApiResponse(
    @JsonProperty("code") String code,
    @JsonProperty("description") String description,
    @JsonProperty("data") T data
  ) {
    this.code = code;
    this.description = description;
    this.data = data;
  }

  private ApiResponse(Result<T> result) {
    ResultCode code = result.getCode();
    this.code = code.getCode();
    this.description = code.getDescription();
    this.data = result.dangerouslyGetData();
  }

  public static <T> ApiResponse<T> of(Result<T> result) {
    return new ApiResponse<>(result);
  }

}
