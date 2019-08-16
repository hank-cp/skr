/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.security;

import lombok.Data;
import org.skr.config.YamlPropertyLoaderFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
@PropertySource(value = "classpath:security.yml",
        factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "spring.skr.security")
@RefreshScope
@Data
public class SkrSecurityProperties {

    /** renew refresh token together every time refresh access token */
    private boolean renewRefreshToken = false;

    private boolean configCors = false;

    /** principal class name to deserialize the JWT token content */
    private Class jwtPrincipalClass;

    /** URLs no need to go through security checks */
    private List<String> skipUrls;

    /** Token setting for user request */
    private Token loginToken = new Token();

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