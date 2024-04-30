package org.pageflow.global.config;

import org.pageflow.global.property.AppProps;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class StaticResourceHandlersConfig implements WebMvcConfigurer {

    private final String webPathPrefix;
    private final String directoryParent;

    public StaticResourceHandlersConfig(AppProps props) throws MalformedURLException {
        this.webPathPrefix = (new URL(props.file.webBaseUrl)).getPath();
        this.directoryParent = props.file.parent;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(webPathPrefix + "/**")
            .addResourceLocations("file:" + directoryParent + "/");
    }
}