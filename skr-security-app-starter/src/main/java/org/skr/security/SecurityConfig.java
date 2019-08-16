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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Configuration
@Import({PermissionCheckingAspect.class, JwtAuthExceptionFilter.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @Autowired
    private JwtAuthExceptionFilter jwtAuthExceptionFilter;

    @Override
    public void configure(WebSecurity web) {
        Set<String> skipUrls = new HashSet<>(skrSecurityProperties.getSkipUrls());
        web.ignoring().antMatchers(skipUrls.toArray(new String[] {}));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Set<String> skipUrls = new HashSet<>(skrSecurityProperties.getSkipUrls());
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(skipUrls.toArray(new String[] {})).permitAll()
                    .anyRequest().authenticated()
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(skrSecurityProperties),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtAuthExceptionFilter,
                            JwtAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtFeignInterceptor jwtFeignInterceptor() {
        return new JwtFeignInterceptor();
    }

}
