package org.pageflow.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pageflow.common.api.HtmlCharacterEscapes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author : sechan
 */
@Configuration
public class HtmlXssEscapeJacksonHttpMessageConverterWebConfig implements WebMvcConfigurer {

  @Autowired
  private ObjectMapper objectMapper;

  public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
    ObjectMapper copy = objectMapper.copy();
    copy.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
    return new MappingJackson2HttpMessageConverter(copy);
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(1, jsonEscapeConverter());
  }
}
