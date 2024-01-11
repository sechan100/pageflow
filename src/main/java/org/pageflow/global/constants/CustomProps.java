package org.pageflow.global.constants;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
@ConfigurationProperties(prefix = "custom")
public class CustomProps {

    private final Site site = new Site();
    private final Email email = new Email();
    private final Files files = new Files();
    private final Defaults defaults = new Defaults();
    private final Admin admin = new Admin();


    @Data
    public static class Site {
        private String baseUrl;
        private String loginFormUri;
    }

    @Data
    public static class Email {
        private String emailVerifySender;
        private String noReplySender;
    }

    @Data
    public static class Files {

        private final Img img = new Img();

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Img {
            private String webUrlPrefix;
            private String directory;
            
            
            public Img(String webUrlPrefix, String directory) {
                this.webUrlPrefix = addPrefixSlashAndRemoveSuffixSlash(webUrlPrefix);
                this.directory = addPrefixSlashAndRemoveSuffixSlash(directory);
                
            }
            
            /**
             * path를 받아서 맨 앞에 /가 없다면 붙이고, 맨 뒤에 /가 있다면 지운다.
             */
            private String addPrefixSlashAndRemoveSuffixSlash(String path) {
                if(path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                if(!path.startsWith("/")) {
                    path = "/" + path;
                }
                return path;
            }
        }
    }
    
    @Data
    public static class Defaults {
        private String defaultUserProfileImg;
        private String defaultBookCoverImg;
    }
    
    @Data
    public static class Admin{
        private String username;
        private String password;
        private String email;
    }
 
}
