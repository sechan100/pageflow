package org.pageflow.common.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;


@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
@ConfigurationProperties(prefix = "pageflow")
public class ApplicationProperties {
  Site site;
  Auth auth;
  User user;
  Book book;
  File file;


  @AllArgsConstructor
  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class Site {
    String protocol;
    String baseUrl;
    String clientUrl;
    String clientProxyPrefix;
  }

  @AllArgsConstructor
  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class Auth {
    String jwtSecret;
    int accessTokenExpireMinutes;
    int refreshTokenExpireDays;
  }

  @AllArgsConstructor
  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class User {
    String defaultProfileImageUrl;
  }

  @AllArgsConstructor
  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class Book {
    String defaultCoverImageUrl;
  }

  @AllArgsConstructor
  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class File {
    /**
     * public 키워드 사용 불가라 _ 붙임
     */
    Public public_;

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
    public static class Public {
      String webBaseUrl;
      String serverDirectory;
    }


  }

}
