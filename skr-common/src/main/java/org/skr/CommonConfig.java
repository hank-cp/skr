package org.skr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skr.common.util.BeanUtil;
import org.skr.config.ApplicationContextProvider;
import org.skr.config.YamlPropertyLoaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import java.lang.reflect.Method;

@SpringBootApplication(scanBasePackages = "org.skr")
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties
@EnableCaching
@PropertySource(value = "classpath:memcached.yml",
        factory = YamlPropertyLoaderFactory.class)
public class CommonConfig {

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
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
            BeanUtil.setupObjectMapper(objectMapper);
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        }
    }

}
