package org.pageflow.user.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

  public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  @Bean
  PasswordEncoder passwordEncoder() {
    return PASSWORD_ENCODER;
  }
}
