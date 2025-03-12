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
public class ApiResponse {

  /**
   * {@link ResultCode}의 name()이다.
   */
  private final String code;
  private final String description;
  @Nullable
  private final Object data;


  @JsonCreator
  public ApiResponse(
    @JsonProperty("code") String code,
    @JsonProperty("description") String description,
    @JsonProperty("data") Object data
  ) {
    this.code = code;
    this.description = description;
    this.data = data;
  }

  private ApiResponse(Result result) {
    ResultCode code = result.getCode();
    this.code = code.getCode();
    this.description = code.getDescription();
    this.data = result.getRawDataWithoutCodeCheck();
  }

  public static ApiResponse of(Result result) {
    return new ApiResponse(result);
  }

}
