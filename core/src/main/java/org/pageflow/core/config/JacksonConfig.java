package org.pageflow.core.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.pageflow.common.user.UID;
import org.pageflow.common.utility.JsonUtility;
import org.pageflow.core.user.UIDDeserializer;
import org.pageflow.core.user.UIDSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = JsonMapper.builder()
      .enable(MapperFeature.USE_STD_BEAN_NAMING)
      .build();

    // Jackson의 커스텀 Serializer/Deserializer를 등록
    SimpleModule module = new SimpleModule();
    module.addSerializer(UID.class, new UIDSerializer());
    module.addDeserializer(UID.class, new UIDDeserializer());
    mapper.registerModule(module);

    // Java 8의 LocalDate, LocalTime, LocalDateTime을 위한 모듈을 등록
    mapper.registerModule(new JavaTimeModule());

    return mapper;
  }

  @Bean
  public JsonUtility jsonUtility(ObjectMapper objectMapper) {
    return new JsonUtility(objectMapper);
  }
}