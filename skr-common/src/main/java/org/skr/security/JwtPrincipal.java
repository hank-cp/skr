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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface JwtPrincipal {

    @NotNull
    String getUsername();

    Boolean isRobot();

    @JsonIgnore
    String getApiTrainJwtToken();

    void setApiTrainJwtToken(String token);

    static JwtPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtPrincipal user = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof JwtPrincipal) {
            user = (JwtPrincipal) authentication.getPrincipal();
        }
        return user;
    }
}
