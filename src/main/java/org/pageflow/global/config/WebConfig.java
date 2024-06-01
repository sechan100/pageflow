package org.pageflow.global.config;

import org.pageflow.global.mvc.LongToTsidConverter;
import org.pageflow.global.mvc.StringToTsidConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToTsidConverter stringToTsidConverter;
    private final LongToTsidConverter longToTsidConverter;

    public WebConfig(StringToTsidConverter stringToTsidConverter, LongToTsidConverter longToTsidConverter) {
        this.stringToTsidConverter = stringToTsidConverter;
        this.longToTsidConverter = longToTsidConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToTsidConverter);
        registry.addConverter(longToTsidConverter);
    }
}