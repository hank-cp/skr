package org.skr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails user = (UserDetails) authentication.getPrincipal();
                return Optional.of(user.getUsername());

            } else {
                throw new RuntimeException("Security Configuration is not setup correctly.");
            }
        }
    }
}
