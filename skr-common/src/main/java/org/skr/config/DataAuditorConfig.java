package org.skr.config;

import org.skr.common.exception.Errors;
import org.skr.common.exception.ConfException;
import org.skr.security.JwtPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing
class DataAuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new DataAuditorAware();
    }

    static class DataAuditorAware implements AuditorAware<String> {

        public String getCurrentAuditor() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                return "∆anonymous∆";
            }

            if (authentication.getPrincipal() instanceof JwtPrincipal) {
                JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
                return principal.username;

            } else {
                throw new ConfException(Errors.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
