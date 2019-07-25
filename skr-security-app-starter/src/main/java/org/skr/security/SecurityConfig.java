package org.skr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

@Configuration
@Import({PermissionCheckingAspect.class, JwtAuthExceptionFilter.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JwtAuthExceptionFilter jwtAuthExceptionFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
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
                    .addFilterBefore(new JwtAuthenticationFilter(skrSecurityProperties, applicationContext),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtAuthExceptionFilter,
                            JwtAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public JwtFeignInterceptor jwtFeignInterceptor() {
        return new JwtFeignInterceptor();
    }

}
