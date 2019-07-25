package org.skr.security;

import lombok.Data;
import org.skr.config.YamlPropertyLoaderFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource(value = "classpath:security.yml",
        factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "spring.skr.security")
@RefreshScope
@Data
public class SkrSecurityProperties {

    /** renew refresh token together every time refresh access token */
    private boolean renewRefreshToken = false;

    /** principal class name to deserialize the JWT token content */
    private Class jwtPrincipalClass;

    /** URLs no need to go through security checks */
    private List<String> skipUrls;

    /** Token setting for user request */
    private Token accessToken = new Token();

    /** Token setting for refresh request */
    private Token refreshToken = new Token();

    /** Token setting for automation api invocation, like batch jobs */
    private Token robotToken = new Token();

    /** Token setting for trained api invocation */
    private Token trainToken = new Token();

    @Data
    public static class Token {
        private String secret = "skr";

        /** token expiration time in minutes */
        private long expiration = 0;

        /** prefix to distinguish toke type */
        private String prefix = "";

        /** http header field to hold the token */
        private String header;

        /** Default user name if the token does not provide one */
        private String username;
    }

}