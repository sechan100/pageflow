package org.pageflow.core.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class ResTestWrapper {
  @Getter
  private final ApiResponse apiResponse;

  public ResTestWrapper isSuccess() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
    assert apiResponse.getCode().equals(CommonCode.SUCCESS.name()):
      "ApiResponse가 실패했습니다(not 'SUCCESS')\n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiResponse);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return this;
  }

  public ResTestWrapper is(ResultCode code){
    assert apiResponse.getCode().equals(code.name()): "Unexpected Code: expected: " + code.name() + ", actual: " + apiResponse.getCode();
    return this;
  }
}
