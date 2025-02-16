package org.pageflow.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.pageflow.common.shared.utility.JsonUtility;
import org.pageflow.common.user.UID;
import org.pageflow.core.user.UIDDeserializer;
import org.pageflow.core.user.UIDSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // Jackson의 커스텀 Serializer/Deserializer를 등록
    SimpleModule module = new SimpleModule();
    module.addSerializer(UID.class, new UIDSerializer());
    module.addDeserializer(UID.class, new UIDDeserializer());
    mapper.registerModule(module);
    mapper.registerModule(new JavaTimeModule());

    return mapper;
  }

  @Bean
  public JsonUtility jsonUtility(ObjectMapper objectMapper) {
    return new JsonUtility(objectMapper);
  }
}