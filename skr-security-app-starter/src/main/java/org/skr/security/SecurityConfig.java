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

@Configuration
@Import({PermissionCheckingAspect.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/actuator/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(skrSecurityProperties, applicationContext),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new JwtAuthExceptionFilter(),
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
