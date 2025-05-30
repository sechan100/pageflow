package org.pageflow.core.controller;

import lombok.Value;
import org.pageflow.book.domain.toc.entity.FolderDesign;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.user.domain.Password;
import org.pageflow.user.port.in.service.PennameValidator;
import org.pageflow.user.port.in.service.UsernameValidator;

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

    // 아이디 관련
    int usernameMinLength = UsernameValidator.MIN_LENGTH;
    int usernameMaxLength = UsernameValidator.MAX_LENGTH;
    String usernameRegex = UsernameValidator.REGEX;
    String usernameRegexMessage = UsernameValidator.REGEX_MESSAGE;

    // 비밀번호 관련
    int passwordMinLength = Password.MIN_LENGTH;
    int passwordMaxLength = Password.MAX_LENGTH;
    String passwordRegex = Password.REGEX;
    String passwordRegexMessage = Password.REGEX_MESSAGE;

    // 필명 관련
    int pennameMinLength = PennameValidator.MIN_LENGTH;
    int pennameMaxLength = PennameValidator.MAX_LENGTH;
    String pennameRegex = PennameValidator.REGEX;
    String pennameRegexMessage = PennameValidator.REGEX_MESSAGE;

  }

  @Value
  public static class Book {
    public Book(ApplicationProperties properties) {
    }

    FolderDesign[] folderDesigns = FolderDesign.values();
  }
}

