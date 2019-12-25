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

import lombok.AllArgsConstructor;
import lombok.Value;
import org.skr.common.util.JsonUtil;
import org.skr.common.util.JwtUtil;

import java.util.function.Function;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Value
@AllArgsConstructor(staticName = "of")
public class Token {

    private String header;

    private UserPrincipal principal;

    private String prefix;

    private long expiration;

    private String secret;

    public static Token of(SkrSecurityProperties.Token tokenProp, UserPrincipal principal) {
        return of(tokenProp.getHeader(),
                principal,
                tokenProp.getPrefix(),
                tokenProp.getExpiration(),
                tokenProp.getSecret());
    }

    public String encode() {
        return prefix+JwtUtil.encode(JsonUtil.toJson(principal), expiration, secret);
    }

    public String encodeIdentity() {
        return prefix+JwtUtil.encode(principal.getIdentity(), expiration, secret);
    }

    public String encode(Function<Token, String> payloadGenerator) {
        return prefix+JwtUtil.encode(payloadGenerator.apply(this), expiration, secret);
    }
}