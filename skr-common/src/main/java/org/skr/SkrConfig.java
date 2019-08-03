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

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
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
