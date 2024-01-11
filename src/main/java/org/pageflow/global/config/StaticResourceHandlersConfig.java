package org.pageflow.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceHandlersConfig implements WebMvcConfigurer {


    @Value("${custom.files.img.directory}")
    private String uploadDirectory;

    @Value("${custom.files.img.base-url}")
    private String baseUrl;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(baseUrl + "/**")
                .addResourceLocations("file:" + uploadDirectory + "/");
    }
}