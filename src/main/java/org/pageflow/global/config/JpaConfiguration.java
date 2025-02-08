package org.pageflow.global.config;

import org.pageflow.shared.jpa.repository.BaseJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author : sechan
 */
@Configuration
@EnableJpaRepositories(
  value = "org.pageflow",
  repositoryBaseClass = BaseJpaRepositoryImpl.class
)
public class JpaConfiguration {
}
