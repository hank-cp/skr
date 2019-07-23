package org.skr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skr.common.util.JsonUtil;
import org.skr.config.ApplicationContextProvider;
import org.skr.security.SkrSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import java.lang.reflect.Method;

@Configuration
@EnableDiscoveryClient
@EnableConfigurationProperties
@Import({SkrSecurityProperties.class, ApplicationContextProvider.class})
public class SkrConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public KeyGenerator cacheKeyGenerator() {
        return new SimpleKeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                String methodPrefix = target.toString() + "∆" + method.getName() + "∆";
                return methodPrefix + super.generate(target, method, params);
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
