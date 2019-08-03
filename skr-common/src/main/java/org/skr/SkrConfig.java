package org.skr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.JsonUtil;
import org.skr.config.GeneralExceptionHandler;
import org.skr.config.json.CustomDeserializer;
import org.skr.security.SkrSecurityProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties
@Import({SkrSecurityProperties.class, GeneralExceptionHandler.class})
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

    @Configuration
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    public static class JacksonConfigurer implements InitializingBean {

        @Autowired
        private ObjectMapper objectMapper;

        @SuppressWarnings("Duplicates")
        @Override
        public void afterPropertiesSet() {
            SimpleModule module = new SimpleModule();
            JsonUtil.setupObjectMapper(objectMapper);
            module.addDeserializer(ErrorInfo.class, new CustomDeserializer<>(ErrorInfo.ErrorInfoImpl.class));
            objectMapper.registerModule(module);
        }
    }

}
