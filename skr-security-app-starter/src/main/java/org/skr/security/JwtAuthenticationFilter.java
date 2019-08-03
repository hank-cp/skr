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
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Resolve {@link JwtAuthenticationToken} from request header
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private SkrSecurityProperties skrSecurityProperties;

    private ApplicationContext applicationContext;

    public JwtAuthenticationFilter(SkrSecurityProperties skrSecurityProperties,
                                   ApplicationContext applicationContext) {
        this.skrSecurityProperties = skrSecurityProperties;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            String accessToken = request.getHeader(
                    skrSecurityProperties.getAccessToken().getHeader());

            if (Checker.isEmpty(accessToken)) {
                throw new AuthException(ErrorInfo.ACCESS_TOKEN_NOT_PROVIDED);
            }

            Authentication authentication = getAuthentication(accessToken);
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

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String accessToken) {
        String prefix;
        String secret;
        if (accessToken.startsWith(skrSecurityProperties.getAccessToken().getPrefix())) {
            prefix = skrSecurityProperties.getAccessToken().getPrefix();
            secret = skrSecurityProperties.getAccessToken().getSecret();
        } else if (accessToken.startsWith(skrSecurityProperties.getRobotToken().getPrefix())) {
            prefix = skrSecurityProperties.getRobotToken().getPrefix();
            secret = skrSecurityProperties.getRobotToken().getSecret();
        } else if (accessToken.startsWith(skrSecurityProperties.getTrainToken().getPrefix())) {
            prefix = skrSecurityProperties.getTrainToken().getPrefix();
            secret = skrSecurityProperties.getTrainToken().getSecret();
        } else {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }

        if (skrSecurityProperties.getJwtPrincipalClass() == null) {
            throw new ConfException(ErrorInfo.CLASS_NOT_FOUND
                    .setMsg("spring.skr.security.jwtPrincipalClass is not specified."));
        }

        return Optional.of(accessToken)
                .map(token -> token.replace(prefix, ""))
                .map(token -> new Tuple2<>(JwtUtil.decode(token, secret), token))
                .map(decodedTuple -> {
                    JwtPrincipal principal = JsonUtil.fromJSON(
                            skrSecurityProperties.getJwtPrincipalClass(), decodedTuple._0);
                    if (Checker.isTrue(principal.isRobot())) {
                        principal.setApiTrainJwtToken(accessToken);
                    } else {
                        principal.setApiTrainJwtToken(
                                skrSecurityProperties.getTrainToken().getPrefix() +
                                        JwtUtil.encode(JsonUtil.toJSON(principal),
                                                skrSecurityProperties.getTrainToken().getExpiration(),
                                                skrSecurityProperties.getTrainToken().getSecret()));
                    }

                    return new JwtAuthenticationToken(principal);
                })
                .orElse(null);
    }

}
