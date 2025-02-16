package org.pageflow.core;

import org.pageflow.common.shared.jpa.repository.BaseJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(
  value = "org.pageflow",
  repositoryBaseClass = BaseJpaRepositoryImpl.class
)
@EntityScan(basePackages = {"org.pageflow"})
@ConfigurationPropertiesScan // @ConfigurationProperties 클래스를 빈으로 등록
public class PageflowApplication {

  public static void main(String[] args) {
    SpringApplication.run(PageflowApplication.class, args);
  }

}
