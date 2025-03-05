package org.pageflow.test.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.pageflow.common.api.ApiResponse;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class TestRes {
  private final ApiResponse apiResponse;
  @Getter
  private final JsonNode data;

  public TestRes isSuccess() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      Assertions.assertEquals(CommonCode.SUCCESS.name(), apiResponse.getCode(), "ApiResponse가 실패했습니다(not 'SUCCESS')\n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiResponse));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return this;
  }

  public TestRes is(ResultCode code, String message) {
    Assertions.assertEquals(code.name(), apiResponse.getCode(), message != null ? message : "Unexpected Code: expected: " + code.name() + ", actual: " + apiResponse.getCode());
    return this;
  }

  public TestRes is(ResultCode code) {
    return is(code, null);
  }

}
