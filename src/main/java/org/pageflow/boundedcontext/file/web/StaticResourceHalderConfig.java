package org.pageflow.boundedcontext.file.web;

import lombok.RequiredArgsConstructor;
import org.pageflow.global.property.AppProps;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaticResourceHalderConfig implements WebMvcConfigurer {

    private final AppProps props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler(props.file.webUriPrefix + "/**")
            .addResourceLocations("file:" + props.file.parent + "/");
    }
}