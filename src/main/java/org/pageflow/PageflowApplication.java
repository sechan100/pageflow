package org.pageflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PageflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(PageflowApplication.class, args);
    }

}
