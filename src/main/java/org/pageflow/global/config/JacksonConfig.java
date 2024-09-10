package org.pageflow.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.pageflow.global.web.TSIDDeserializer;
import org.pageflow.global.web.TSIDSerializer;
import org.pageflow.shared.type.TSID;
import org.pageflow.shared.utility.JsonUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Jackson의 커스텀 Serializer/Deserializer를 등록
        SimpleModule module = new SimpleModule();
        module.addSerializer(TSID.class, new TSIDSerializer());
        module.addDeserializer(TSID.class, new TSIDDeserializer());
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());
        
        return mapper;
    }

    @Bean
    public JsonUtility jsonUtility(ObjectMapper objectMapper) {
        return new JsonUtility(objectMapper);
    }
}