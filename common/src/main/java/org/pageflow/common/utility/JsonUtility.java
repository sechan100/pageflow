package org.pageflow.common.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

/**
 * 빈으로 제작
 *
 * @author : sechan
 */
@RequiredArgsConstructor
public class JsonUtility {
  private final ObjectMapper mapper;

  public ObjectMapper getObjectMapper() {
    return mapper;
  }

  public String toJson(Object object) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch(Exception e){
      throw new IllegalArgumentException(e);
    }
  }
}
