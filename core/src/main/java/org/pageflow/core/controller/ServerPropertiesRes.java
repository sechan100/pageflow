package org.pageflow.core.controller;

import lombok.Value;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.user.domain.Password;
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
    // refresh token 만료 기간
    int refreshTokenExpireDays;

    // 필명 관련
    int pennameMinLength = PennameValidator.MIN_LENGTH;
    int pennameMaxLength = PennameValidator.MAX_LENGTH;
    String pennameRegex = PennameValidator.REGEX;
    String pennameRegexMessage = PennameValidator.REGEX_MESSAGE;

    // 비밀번호 관련
    int passwordMinLength = Password.MIN_LENGTH;
    int passwordMaxLength = Password.MAX_LENGTH;
    String passwordRegex = Password.REGEX;
    String passwordRegexMessage = Password.REGEX_MESSAGE;
  }
}

