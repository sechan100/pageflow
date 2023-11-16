package org.pageflow.base.constants;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {

    private final Site site = new Site();
    private final Email email = new Email();
    private final Files files = new Files();


    @Data
    public static class Site {
        private String baseUrl;
        private String loginFormUri;
    }

    @Data
    public static class Email {
        private String emailVerifySender;
    }

    @Data
    public static class Files {

        private final Img img = new Img();

        @Data
        public static class Img {
            private String baseUrl;
            private String directory;
        }
    }
}
