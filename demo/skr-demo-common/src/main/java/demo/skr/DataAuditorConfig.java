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
package demo.skr;

import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.security.JwtPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Configuration
@EnableJpaAuditing
class DataAuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new DataAuditorAware();
    }

    static class DataAuditorAware implements AuditorAware<String> {

        public Optional<String> getCurrentAuditor() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                return Optional.of("∆anonymous∆");
            }

            if (authentication.getPrincipal() instanceof JwtPrincipal) {
                JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
                return Optional.of(principal.getName());

            } else {
                throw new ConfException(ErrorInfo.INCOMPATIBLE_TYPE.msgArgs(
                        JwtPrincipal.class.getName(),
                        authentication.getPrincipal().getClass()));
            }
        }
    }
}
