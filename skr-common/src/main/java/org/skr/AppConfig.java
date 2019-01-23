package org.skr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skr.common.util.BeanUtil;
import org.skr.config.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "org.skr")
public class AppConfig {

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

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            BeanUtil.setupObjectMapper(objectMapper);
        }
    }

}
