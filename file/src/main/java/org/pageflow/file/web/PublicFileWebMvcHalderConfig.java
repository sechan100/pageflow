package org.pageflow.file.web;

import org.pageflow.common.property.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class PublicFileWebMvcHalderConfig implements WebMvcConfigurer {

  private final String webBaseUrl;
  private final String serverDirectory;

  @Autowired
  public PublicFileWebMvcHalderConfig(ApplicationProperties props) throws MalformedURLException {
    this.webBaseUrl = (new URL(props.file.public_.webBaseUrl)).getPath();
    this.serverDirectory = props.file.public_.serverDirectory;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(webBaseUrl + "/**")
      .addResourceLocations("file:" + serverDirectory + "/");
  }
}