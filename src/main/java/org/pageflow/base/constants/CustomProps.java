package org.pageflow.base.constants;

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
            private String baseUrl;
            private String directory;
            
            
            public Img(String baseUrl, String directory) {
                if(baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                if(directory.endsWith("/")) {
                    directory = directory.substring(0, directory.length() - 1);
                }
                
                this.baseUrl = baseUrl;
                this.directory = directory;
                
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
