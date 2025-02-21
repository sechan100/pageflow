package org.pageflow.common.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pageflow.common.utility.UriUtility;
import org.springframework.boot.context.properties.ConfigurationProperties;


@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
@ConfigurationProperties(prefix = "pageflow")
public class ApplicationProperties {
  Site site;
  Auth auth;
  User user;
  Book book;
  Email email;
  Admin admin;
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
  public static class Email {
    From from;

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
    public static class From {
      String noReply;
      String defaultFromName;
    }
  }

  @AllArgsConstructor
  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class Admin {
    String username;
    String password;
    String penname;
    String email;
  }

  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class File {
    String webBaseUrl;
    String parent;

    public File(String webBaseUrl, String parent) {
      this.webBaseUrl = webBaseUrl;
      this.parent = UriUtility.addStartSlashAndRemoveEndSlash(parent);
    }

  }

}
