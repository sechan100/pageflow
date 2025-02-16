package org.pageflow.file.web;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.property.ApplicationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaticResourceHalderConfig implements WebMvcConfigurer {

  private final ApplicationProperties props;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
      .addResourceHandler(props.file.webBaseUrl + "/**")
      .addResourceLocations("file:" + props.file.parent + "/");
  }
}