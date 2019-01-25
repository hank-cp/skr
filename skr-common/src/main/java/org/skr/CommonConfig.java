package org.skr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skr.common.util.BeanUtil;
import org.skr.config.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication(scanBasePackages = "org.skr")
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties
public class CommonConfig {

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
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
