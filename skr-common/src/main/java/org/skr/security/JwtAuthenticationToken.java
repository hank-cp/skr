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

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.Checker;
import org.skr.common.util.JsonUtil;
import org.skr.common.util.JwtUtil;
import org.skr.common.util.tuple.Tuple2;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Slf4j
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtPrincipal principal;

    public JwtAuthenticationToken(@NotNull JwtPrincipal principal) {
        super(Collections.emptyList());
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public String getName() {
        return this.principal.getName();
    }

    public static Authentication authenticate(String accessToken, SkrSecurityProperties properties) {
        try {
            if (Checker.isEmpty(accessToken)) {
                throw new AuthException(ErrorInfo.ACCESS_TOKEN_NOT_PROVIDED);
            }

            Authentication authentication = getAuthentication(accessToken, properties);
            if (!authentication.isAuthenticated()) {
                throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (TokenExpiredException ex) {
            throw new AuthException(ErrorInfo.ACCESS_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(ErrorInfo.ACCESS_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }
    }

    public static Authentication getAuthentication(String accessToken, SkrSecurityProperties properties) {
        if (accessToken.startsWith(properties.getAccessToken().getPrefix())) {
            // decode user principal
            String prefix = properties.getAccessToken().getPrefix();
            String secret = properties.getAccessToken().getSecret();

            if (properties.getJwtPrincipalClass() == null) {
                throw new ConfException(ErrorInfo.MISSING_PROPERTY
                        .msgArgs("spring.skr.security.jwtPrincipalClass"));
            }

            return Optional.of(accessToken)
                    .map(token -> token.replace(prefix, ""))
                    .map(token -> new Tuple2<>(JwtUtil.decode(token, secret), token))
                    .map(decodedTuple -> {
                        JwtPrincipal principal = JsonUtil.fromJson(
                                properties.getJwtPrincipalClass(), decodedTuple._0);
                        return new JwtAuthenticationToken(principal);
                    })
                    .orElse(null);

        } else if (accessToken.startsWith(properties.getGhostToken().getPrefix())) {
            // decode ghost principal
            String prefix = properties.getGhostToken().getPrefix();
            String secret = properties.getGhostToken().getSecret();

            return Optional.of(accessToken)
                    .map(token -> token.replace(prefix, ""))
                    .map(token -> new Tuple2<>(JwtUtil.decode(token, secret), token))
                    .map(decodedTuple -> {
                        JwtPrincipal principal = JsonUtil.fromJson(GhostJwtPrincipal.class, decodedTuple._0);
                        return new JwtAuthenticationToken(principal);
                    })
                    .orElse(null);

        } else {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }
    }

}