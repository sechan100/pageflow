package org.pageflow.base.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.context.annotation.Configuration;



@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Pageflow API",
                version = "0.0.1",
                description = "Pageflow API Description"
        )
)
@Configuration
public class SwaggerConfig {

}