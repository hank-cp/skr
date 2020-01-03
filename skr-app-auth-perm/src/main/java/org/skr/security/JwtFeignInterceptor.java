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

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class JwtFeignInterceptor implements RequestInterceptor {

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @Override
    public void apply(RequestTemplate template) {
        Optional<JwtPrincipal> jwtPrincipal = JwtPrincipal.getCurrentPrincipal();
        if (jwtPrincipal.isEmpty()) {
            template.header(skrSecurityProperties.getAccessToken().getHeader(),
                Token.of(skrSecurityProperties.getAccessToken().getHeader(),
                        GhostJwtPrincipal.of(skrSecurityProperties.getGhostUserName()),
                        skrSecurityProperties.getGhostToken().getPrefix(),
                        skrSecurityProperties.getGhostToken().getExpiration(),
                        skrSecurityProperties.getGhostToken().getSecret()).encode());
        } else {
            template.header(skrSecurityProperties.getAccessToken().getHeader(),
                    jwtPrincipal.get().getChainAccessToken());
        }
    }
}
