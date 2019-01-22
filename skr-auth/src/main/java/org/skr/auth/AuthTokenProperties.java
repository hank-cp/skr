package org.skr.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:auth.yml")
@ConfigurationProperties(prefix = "skr.auth.token")
public class AuthTokenProperties {

    @Value("${secret}")
    private String secret = "";

    @Value("${expiration}")
    private long expiration = 1200000;

    @Value("${prefix}")
    private String prefix = "";

    @Value("${header}")
    private String header = "";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
