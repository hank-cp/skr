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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.Checker;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Resolve {@link JwtAuthenticationToken} from request header
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final SkrSecurityProperties skrSecurityProperties;

    private final List<String> whitelistIps;

    public JwtAuthenticationFilter(SkrSecurityProperties skrSecurityProperties) {
        this.skrSecurityProperties = skrSecurityProperties;
        whitelistIps = Optional.ofNullable(skrSecurityProperties.getGhostWhitelistIps()).orElse(List.of());
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        if (!((request instanceof HttpServletRequest httpRequest) && (response instanceof HttpServletResponse httpResponse))) {
            throw new ServletException("OncePerRequestFilter only supports HTTP requests");
        }

        // ignore websocket
        if (httpRequest.getHeader("sec-websocket-protocol") == null
            && httpRequest.getHeader("sec-websocket-key") == null) {
            String accessToken = httpRequest.getHeader(skrSecurityProperties.getAccessToken().getHeader());
            if (accessToken == null && !Checker.isEmpty(httpRequest.getCookies())) {
                // get access token from cookies
                accessToken = Arrays.stream(httpRequest.getCookies())
                    .filter(cookie -> cookie.getName().equals(skrSecurityProperties.getAccessToken().getHeader()))
                    .map(Cookie::getValue).findAny().orElse(null);
            }

            if (!Checker.isEmpty(accessToken)
                && accessToken.startsWith(skrSecurityProperties.getGhostToken().getPrefix())
                && !validateRemoteIp(request.getRemoteAddr())) {
                // validate whether ip using ghost token is valid.
                throw new AuthException(ErrorInfo.CLIENT_IP_NOT_ALLOWED.msgArgs(request.getRemoteAddr()));
            }
            JwtAuthenticationToken.authenticate(accessToken, skrSecurityProperties);
        }
        filterChain.doFilter(request, response);
    }

    public boolean validateRemoteIp(String remoteIp) {
        return this.whitelistIps.stream().map(IpAddressMatcher::new).anyMatch(matcher -> matcher.matches(remoteIp));
    }

}
