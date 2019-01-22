package org.skr.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.skr.config.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Configuration
public class AppConfig {

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Component
    public static class ApplicationEventListener implements ApplicationListener<ContextRefreshedEvent> {

        @Autowired
        private ObjectMapper objectMapper;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            // Additional Config for Jackson ObjectMapper
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            SimpleModule module = new SimpleModule();
            module.addSerializer(IntValuedEnum.class, new IntValuedEnumSerializer());
            module.addSerializer(StringValuedEnum.class, new StringValuedEnumSerializer());
            module.addSerializer(Stream.class, new StreamSerializer());
            objectMapper.registerModule(module);

            objectMapper.setVisibility(objectMapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                    .withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY));
        }
    }

}
