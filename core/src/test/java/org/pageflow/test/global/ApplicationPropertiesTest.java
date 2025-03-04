package org.pageflow.test.global;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.test.e2e.config.PageflowIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * properties가 생성될 때, 잘못된 속성값이 있는지 검증한다.
 * @author : sechan
 */
@PageflowIntegrationTest
public class ApplicationPropertiesTest {
  @Autowired
  private ApplicationProperties properties;

  @Test
  @DisplayName("properties에 환경변수가 올바르게 바인딩되었는지 검증")
  void shouldBindEnvironmentVariable() {
    List<String> stringFields = extractStringFields(properties);
    for(String stringField : stringFields) {
      if(stringField.matches("\\$\\{.*}")) {
        throw new IllegalArgumentException("properties에 환경변수가 올바르게 바인딩되지 않았습니다: " + stringField + "만약 의도된 값인 경우 다른 방식을 통해 바인딩해주세요.");
      }
    }
  }

  private List<String> extractStringFields(Object object) {
    List<String> stringValues = new ArrayList<>();
    if(object == null) {
      return stringValues;
    }
    // 현재 클래스의 모든 필드 조회
    Class<?> clazz = object.getClass();
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      try {
        Object value = field.get(object);
        // String 타입인 필드만 추출
        if(value instanceof String) {
          stringValues.add((String) value);
        // String이 아니라면 재귀 호출(중첩 properties)
        } else if (value != null && !field.getType().isPrimitive()) {
          stringValues.addAll(extractStringFields(value));
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return stringValues;
  }
}
