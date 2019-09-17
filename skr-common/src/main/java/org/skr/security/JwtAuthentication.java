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
import org.apache.commons.lang3.exception.ExceptionUtils;
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
public class JwtAuthentication {

    public static void authenticate(String accessToken, SkrSecurityProperties properties) {
        try {
            if (Checker.isEmpty(accessToken)) {
                throw new AuthException(ErrorInfo.ACCESS_TOKEN_NOT_PROVIDED);
            }

            Authentication authentication = getAuthentication(accessToken, properties);
            if (!authentication.isAuthenticated()) {
                throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (TokenExpiredException ex) {
            throw new AuthException(ErrorInfo.ACCESS_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(ErrorInfo.ACCESS_TOKEN_BROKEN);
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }
    }

    public static Authentication getAuthentication(String accessToken, SkrSecurityProperties properties) {
        String prefix;
        String secret;
        if (accessToken.startsWith(properties.getAccessToken().getPrefix())) {
            prefix = properties.getAccessToken().getPrefix();
            secret = properties.getAccessToken().getSecret();
        } else if (accessToken.startsWith(properties.getRobotToken().getPrefix())) {
            prefix = properties.getRobotToken().getPrefix();
            secret = properties.getRobotToken().getSecret();
        } else if (accessToken.startsWith(properties.getTrainToken().getPrefix())) {
            prefix = properties.getTrainToken().getPrefix();
            secret = properties.getTrainToken().getSecret();
        } else {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }

        if (properties.getJwtPrincipalClass() == null) {
            throw new ConfException(ErrorInfo.REQUIRED_PROPERTY_NOT_SET
                    .msgArgs("spring.skr.security.jwtPrincipalClass"));
        }

        return Optional.of(accessToken)
                .map(token -> token.replace(prefix, ""))
                .map(token -> new Tuple2<>(JwtUtil.decode(token, secret), token))
                .map(decodedTuple -> {
                    JwtPrincipal principal = JsonUtil.fromJSON(
                            properties.getJwtPrincipalClass(), decodedTuple._0);
                    if (Checker.isTrue(principal.isRobot())) {
                        principal.setApiTrainJwtToken(accessToken);
                    } else {
                        principal.setApiTrainJwtToken(
                                properties.getTrainToken().getPrefix() +
                                        JwtUtil.encode(JsonUtil.toJSON(principal),
                                                properties.getTrainToken().getExpiration(),
                                                properties.getTrainToken().getSecret()));
                    }

                    return new JwtAuthenticationToken(principal);
                })
                .orElse(null);
    }

    public static class JwtAuthenticationToken extends AbstractAuthenticationToken {

        private final JwtPrincipal principal;

        public JwtAuthenticationToken(@NotNull JwtPrincipal principal) {
            super(Collections.emptyList());

            if (principal == null) {
                throw new IllegalArgumentException(
                        "Cannot pass null or empty values to constructor");
            }

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
    }
}