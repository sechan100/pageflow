package org.pageflow.core.config;

import org.pageflow.common.property.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class StaticResourceHandlersConfig implements WebMvcConfigurer {

  private final String webPathPrefix;
  private final String directoryParent;

  @Autowired
  public StaticResourceHandlersConfig(ApplicationProperties props) throws MalformedURLException {
    this.webPathPrefix = (new URL(props.file.webBaseUrl)).getPath();
    this.directoryParent = props.file.parent;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(webPathPrefix + "/**")
      .addResourceLocations("file:" + directoryParent + "/");
  }
}