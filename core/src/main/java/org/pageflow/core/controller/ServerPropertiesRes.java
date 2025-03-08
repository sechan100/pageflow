package org.pageflow.core.controller;

import lombok.Value;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.user.domain.service.PennameValidator;

/**
 * @author : sechan
 */
@Value
public class ServerPropertiesRes {
  User user;

  public ServerPropertiesRes(ApplicationProperties properties) {
    this.user = new User(properties);
  }

  @Value
  public static class User {
    public User(ApplicationProperties properties) {
      this.refreshTokenExpireDays = properties.auth.refreshTokenExpireDays;
    }

    int refreshTokenExpireDays;
    int pennameMinLength = PennameValidator.MIN_LENGTH;
    int pennameMaxLength = PennameValidator.MAX_LENGTH;
    String pennameRegex = PennameValidator.REGEX;
    String pennameRegexMessage = PennameValidator.REGEX_MESSAGE;
  }
}

