package org.skr.security;

import lombok.Data;
import org.skr.config.YamlPropertyLoaderFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource(value = "classpath:security.yml",
        factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "skr.security")
@Data
public class SecurityProperties {

    private Token accessToken = new Token();

    private Token refreshToken = new Token();

    @Data
    public static class Token {
        private String secret;

        private long expiration = 1200000;

        private String prefix = "";

        private String header = "";
    }

}