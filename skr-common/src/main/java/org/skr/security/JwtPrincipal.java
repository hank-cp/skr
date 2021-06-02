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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.laxture.spring.util.ApplicationContextProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface JwtPrincipal extends UserPrincipal {

    @JsonIgnore
    Boolean isGhost();

    /**
     * make a new accessToken that will never be expired for internal
     * chain service call.
     */
    @JsonIgnore
    default String getChainAccessToken() {
        SkrSecurityProperties skrSecurityProperties = ApplicationContextProvider
                .getBean(SkrSecurityProperties.class);
        return isGhost() ? Token.of(skrSecurityProperties.getAccessToken().getHeader(),
                GhostJwtPrincipal.of(skrSecurityProperties.getGhostUserName()),
                skrSecurityProperties.getGhostToken().getPrefix(),
                skrSecurityProperties.getGhostToken().getExpiration(),
                skrSecurityProperties.getGhostToken().getSecret()).encode()
            : Token.of(
                skrSecurityProperties.getAccessToken().getHeader(),
                this,
                skrSecurityProperties.getAccessToken().getPrefix(),
                0L,
                skrSecurityProperties.getAccessToken().getSecret()).encode();
    }

    static Optional<JwtPrincipal> getCurrentPrincipal() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof JwtPrincipal)
                .map(principal -> (JwtPrincipal) principal);
    }

}
