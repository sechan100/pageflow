package org.pageflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan // @ConfigurationProperties 클래스를 빈으로 등록
public class PageflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(PageflowApplication.class, args);
    }

}
