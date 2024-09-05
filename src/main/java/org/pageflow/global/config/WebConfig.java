package org.pageflow.global.config;

import org.pageflow.global.mvc.StringToTsidConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToTsidConverter stringToTsidConverter;

    public WebConfig(StringToTsidConverter stringToTsidConverter) {
        this.stringToTsidConverter = stringToTsidConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToTsidConverter);
    }
}