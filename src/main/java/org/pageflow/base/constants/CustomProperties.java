package org.pageflow.base.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter @Setter
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
    
    private final Site site = new Site();
    private final Email email = new Email();
    
    
    @Getter @Setter
    public static class Site {
        private String baseUrl;
    }
    
    @Getter @Setter
    public static class Email {
        private String emailVerifySender;
    }
    
}
