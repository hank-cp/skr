package org.skr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skr.common.util.JsonUtil;
import org.skr.config.ApplicationContextProvider;
import org.skr.security.SkrSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties
@Import({SkrSecurityProperties.class, ApplicationContextProvider.class})
public class SkrConfig {

    @Bean
    @ConditionalOnMissingBean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            String targetClass = method.getDeclaringClass().getName();
            String methodPrefix = targetClass + "∆" + method.getName() + "∆";
            if (params.length == 0) {
                return methodPrefix + SimpleKey.EMPTY;
            } else {
                return methodPrefix + Arrays.stream(params)
                        .map(param -> {
                            if (param == null) return "null";
                            if (param.getClass().isArray()) return "array";
                            return param.toString();
                        }).collect(Collectors.joining("∆"));
            }
        };
    }

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Component
    public static class ApplicationEventListener implements ApplicationListener<ContextRefreshedEvent> {

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private DispatcherServlet dispatcherServlet;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            JsonUtil.setupObjectMapper(objectMapper);
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        }
    }

}
