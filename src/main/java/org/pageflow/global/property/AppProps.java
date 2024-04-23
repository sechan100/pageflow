package org.pageflow.global.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pageflow.shared.utility.UriUtility;
import org.springframework.boot.context.properties.ConfigurationProperties;


@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
@ConfigurationProperties(prefix = "custom")
public class AppProps {
    Site site;
    Auth auth;
    User user;
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
        String defaultProfileImageUri;
    }

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
    public static class Email {
        From from;

        @AllArgsConstructor
        @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
        public static class From {
            String noReply;
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
        String webUriPrefix;
        String parent;

        public File(String webUriPrefix, String parent) {
            this.webUriPrefix = UriUtility.addStartSlashAndRemoveEndSlash(webUriPrefix);
            this.parent = UriUtility.addStartSlashAndRemoveEndSlash(parent);
        }

    }
    
}
