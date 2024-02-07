package org.pageflow.global.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "custom")
public record CustomProps(Site site, Email email, Files files, Defaults defaults, Admin admin) {
    
    public record Site(String baseUrl, Integer accessTokenExpireMinutes, Integer refreshTokenExpireDays) {}
    
    public record Email(String emailVerifySender, String noReplySender) {}
    
    public record Files(Img img) {
        public record Img(String webUrlPrefix, String directory) {
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
    
    public record Defaults(String userProfileImg, String bookCoverImg) {}
    
    public record Admin(String username, String password, String email) {}
    
}
