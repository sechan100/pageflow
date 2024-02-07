package org.pageflow.global.config;

import lombok.RequiredArgsConstructor;
import org.pageflow.global.constants.CustomProps;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaticResourceHandlersConfig implements WebMvcConfigurer {
    
    private final CustomProps props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(props.files().img().webUrlPrefix() + "/**")
                .addResourceLocations("file:" + props.files().img().directory() + "/");
    }
}